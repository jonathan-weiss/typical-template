package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandAttributeKey.*
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.resolver.CommandContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TextContentPart
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers.Companion.EMPTY_LINE_NUMBERS

object CommandChainCreator {

    private const val DEFAULT_PACKAGE_NAME = ""

    fun validateAndInterpretContentParts(templateContentParts: List<TemplateContentPart>): List<TemplateRendererDescription> {
        val (outerFragments, nestedSections) = splitNestedTemplateRenderers(templateContentParts)
        val result = mutableListOf<TemplateRendererDescription>()
        result.add(validateAndInterpretSingleTemplate(outerFragments))
        for (nestedSection in nestedSections) {
            result.addAll(validateAndInterpretContentParts(nestedSection))
        }
        return result
    }

    private fun validateAndInterpretSingleTemplate(templateContentParts: List<TemplateContentPart>): TemplateRendererDescription {
        val templateRendererKeywordCommand: KeywordCommand = assureFirstAndOnlyCommandIsTemplateDefinition(templateContentParts)
        val templateModels = assureNoDuplicateModelNames(templateContentParts)
        val templateRendererClassDescription = templateRendererKeywordCommand
            .toClassDescription(
                classNameAttribute = TEMPLATE_RENDERER_CLASS_NAME,
                packageNameAttribute = TEMPLATE_RENDERER_PACKAGE_NAME
            )

        val templateRendererInterfaceDescription = templateRendererKeywordCommand
            .toOptionalClassDescription(
                classNameAttribute = TEMPLATE_RENDERER_INTERFACE_NAME,
                packageNameAttribute = TEMPLATE_RENDERER_INTERFACE_PACKAGE_NAME
            )

        val remainingFragments = templateContentParts
            .filterNot { it.isTemplateDefinitionCommand() }
            .filterNot { it.isModelDefinitionCommand() }
            .filterNot { it.isEndTemplateRendererCommand() }


        validateNestingLevelOfFragments(remainingFragments)

        val templateChainItems = adaptMutualInfluencedFragments(remainingFragments)

        return TemplateRendererDescription(
            templateRendererClass = templateRendererClassDescription,
            templateRendererInterface = templateRendererInterfaceDescription,
            modelClasses = templateModels,
            templateChain = templateChainItems
        )
    }

    private data class SplitResult(
        val outerFragments: List<TemplateContentPart>,
        val nestedSections: List<List<TemplateContentPart>>,
    )

    private fun splitNestedTemplateRenderers(templateContentParts: List<TemplateContentPart>): SplitResult {
        val outerFragments = mutableListOf<TemplateContentPart>()
        val nestedSections = mutableListOf<List<TemplateContentPart>>()
        var foundTopLevel = false
        var depth = 0
        var currentNestedSection: MutableList<TemplateContentPart>? = null

        for (fragment in templateContentParts) {
            if (currentNestedSection != null) {
                // We are inside a nested section
                if (fragment.isTemplateDefinitionCommand()) {
                    depth++
                    currentNestedSection.add(fragment)
                } else if (fragment.isEndTemplateRendererCommand()) {
                    if (depth > 0) {
                        depth--
                        currentNestedSection.add(fragment)
                    } else {
                        // Close this nested section
                        nestedSections.add(currentNestedSection)
                        currentNestedSection = null
                    }
                } else {
                    currentNestedSection.add(fragment)
                }
            } else {
                // We are in the outer section
                if (fragment.isTemplateDefinitionCommand()) {
                    if (!foundTopLevel) {
                        foundTopLevel = true
                        outerFragments.add(fragment)
                    } else {
                        // Start a new nested section
                        currentNestedSection = mutableListOf(fragment)
                        depth = 0
                    }
                } else if (fragment.isEndTemplateRendererCommand()) {
                    if (!foundTopLevel) {
                        throw TemplateParsingException(
                            lineNumbers = fragment.lineNumbers,
                            msg = "Found '${CommandKey.END_TEMPLATE_RENDERER.keyword}' without a corresponding " +
                                    "'${CommandKey.TEMPLATE_RENDERER.keyword}' command.",
                        )
                    }
                    // Top-level end-template-renderer: optional, but nothing may follow
                    outerFragments.add(fragment)
                    val remainingIndex = templateContentParts.indexOf(fragment) + 1
                    val remainingFragments = templateContentParts.subList(remainingIndex, templateContentParts.size)
                    val remainingCommands = remainingFragments.filterIsInstance<CommandContentPart>()
                    if (remainingCommands.isNotEmpty()) {
                        throw TemplateParsingException(
                            lineNumbers = remainingCommands.first().lineNumbers,
                            msg = "No commands are allowed after the top-level '${CommandKey.END_TEMPLATE_RENDERER.keyword}'.",
                        )
                    }
                    return SplitResult(outerFragments, nestedSections)
                } else {
                    outerFragments.add(fragment)
                }
            }
        }

        if (currentNestedSection != null) {
            // Find the template-renderer command in the unclosed section for better error reporting
            val nestedRendererFragment = currentNestedSection.firstOrNull { it.isTemplateDefinitionCommand() }
            throw TemplateParsingException(
                lineNumbers = nestedRendererFragment?.lineNumbers ?: EMPTY_LINE_NUMBERS,
                msg = "Nested '${CommandKey.TEMPLATE_RENDERER.keyword}' must be closed with " +
                        "'${CommandKey.END_TEMPLATE_RENDERER.keyword}'.",
            )
        }

        return SplitResult(outerFragments, nestedSections)
    }

    private fun validateNestingLevelOfFragments(contentParts: List<TemplateContentPart>) {
        val openingCommandKeysStack: MutableList<CommandKey> = mutableListOf()

        contentParts.filterIsInstance<CommandContentPart>().forEach { commandFragment ->
            val commandKey = commandFragment.keywordCommand.commandKey
            if(commandKey.isTriggerAutoclose) {
                autocloseNestedStackElements(commandKey, openingCommandKeysStack)
            }
            if(commandKey.isOpeningCommand) {
                openingCommandKeysStack.add(commandKey)
            } else if(commandKey.isClosingCommand) {
                validateClosingCommand(commandFragment, openingCommandKeysStack)
                openingCommandKeysStack.removeLast()
            }
            if(commandKey.isRequiredDirectlyNestedInOtherCommand) {
                validateDirectlyNestedCommand(commandFragment, openingCommandKeysStack)
            }
        }

        if(openingCommandKeysStack.filterNot { it.isAutoclosingSupported }.isNotEmpty()) {
            throw TemplateParsingException(
                lineNumbers = EMPTY_LINE_NUMBERS,
                msg = "The template has the following opening commands ${openingCommandKeysStack.map { it.keyword }} " +
                        "that needs to be closed using the following closing commands " +
                        "${openingCommandKeysStack.map { it.correspondingClosingCommandKey?.keyword }}."
            )
        }

    }

    private fun validateDirectlyNestedCommand(
        commandFragment: CommandContentPart,
        openingCommandKeysStack: List<CommandKey>,
    ) {
        val commandKey = commandFragment.keywordCommand.commandKey
        val requiredEnclosingCommandKey = requireNotNull(commandKey.directlyNestedInsideCommandKey)

        if(openingCommandKeysStack.lastOrNull() != requiredEnclosingCommandKey) {
            throw TemplateParsingException(
                lineNumbers = commandFragment.lineNumbers,
                msg = "The command '${commandKey.keyword}' must reside as directly nested command " +
                        "inside the command '${requiredEnclosingCommandKey.keyword}'."
            )
        }
    }

    private fun autocloseNestedStackElements(
        commandKey: CommandKey,
        openingCommandKeysStack: MutableList<CommandKey>,
    ) {
        require(commandKey.isTriggerAutoclose)
        val correspondingOpeningCommandKey = requireNotNull(commandKey.correspondingOpeningCommandKeyForAutoclose)
        while (openingCommandKeysStack.isNotEmpty()) {
            val lastCommandKey = openingCommandKeysStack.last()
            if(lastCommandKey == correspondingOpeningCommandKey) {
                return
            }
            // here we continue and the lastCommandKey is autoclosed
            openingCommandKeysStack.removeLast()
        }
    }

    private fun validateClosingCommand(
        commandFragment: CommandContentPart,
        openingCommandKeysStack: List<CommandKey>,
    ) {
        val closingCommandKey = commandFragment.keywordCommand.commandKey
        val correspondingOpeningCommandKey = requireNotNull(closingCommandKey.correspondingOpeningCommandKey)
        val lastCommandKey = openingCommandKeysStack.lastOrNull()
        if(lastCommandKey == null || lastCommandKey != correspondingOpeningCommandKey) {
            throw TemplateParsingException(
                lineNumbers = commandFragment.lineNumbers,
                msg = "The template has a closing command '${closingCommandKey.keyword}' " +
                        "without a corresponding opening command '${correspondingOpeningCommandKey.keyword}'" +
                        "before in the template."
            )
        }
    }

    private fun adaptMutualInfluencedFragments(contentParts: List<TemplateContentPart>): List<ChainItem> {
        val templateChainItems = mutableListOf<ChainItem>()
        contentParts.forEachIndexed { index, templateFragment ->
            when (templateFragment) {
                is CommandContentPart -> {
                    when (templateFragment.keywordCommand.commandKey) {
                        CommandKey.STRIP_LINE_BEFORE_COMMENT,
                        CommandKey.STRIP_LINE_AFTER_COMMENT,
                             -> Unit
                        else -> templateChainItems.add(CommandChainItem(keywordCommand = templateFragment.keywordCommand))
                    }
                }
                is TextContentPart -> {
                    templateChainItems.add(createPlainTextChainItem(templateFragment, index, contentParts))
                }
            }
        }
        return templateChainItems
    }

    private fun createPlainTextChainItem(
        templateFragment: TextContentPart,
        index: Int,
        contentParts: List<TemplateContentPart>
    ): PlainTextChainItem {
        val hasPrecedingStripLineAfterComment = contentParts
            .subList(0, index)
            .reversed()
            .hasAnyFollowingCommandBeforeNextText(CommandKey.STRIP_LINE_AFTER_COMMENT)

        val hasFollowingStripLineBeforeComment = contentParts
            .subList(index + 1, contentParts.size)
            .hasAnyFollowingCommandBeforeNextText(CommandKey.STRIP_LINE_BEFORE_COMMENT)

        return PlainTextChainItem(
            text = templateFragment.text,
            removeFirstLineIfWhitespaces = hasPrecedingStripLineAfterComment,
            removeLastLineIfWhitespaces = hasFollowingStripLineBeforeComment,
        )
    }

    private fun List<TemplateContentPart>.hasAnyFollowingCommandBeforeNextText(commandKey: CommandKey): Boolean {
        return this.takeWhile { it !is TextContentPart }
            .filterIsInstance<CommandContentPart>()
            .any { it.keywordCommand.commandKey == commandKey }
    }

    private fun assureNoDuplicateModelNames(
        templateContentParts: List<TemplateContentPart>
    ): List<ModelDescription> {

        val modelFragments = templateContentParts
            .filterIsInstance<CommandContentPart>()
            .filter { it.isModelDefinitionCommand() }

        val usedModelNames = mutableSetOf<String>()
        for(modelFragment in modelFragments) {
            val modelDescriptions = modelFragment.keywordCommand.toModelDescription()
            for(modelDescription in modelDescriptions) {
                if(modelDescription.modelName in usedModelNames) {
                    throw TemplateParsingException(
                        lineNumbers = modelFragment.lineNumbers,
                        msg = "The model name ${modelDescription.modelName} is used more than once."
                    )
                }
                usedModelNames.add(modelDescription.modelName)
            }
        }
        return modelFragments.flatMap { it.keywordCommand.toModelDescription() }
    }


    private fun assureFirstAndOnlyCommandIsTemplateDefinition(
        templateContentParts: List<TemplateContentPart>
    ): KeywordCommand {
        val count = templateContentParts.count { it.isTemplateDefinitionCommand() }

        if(count != 1) {
            throw TemplateParsingException(
                lineNumbers = templateContentParts.firstOrNull()?.lineNumbers ?: EMPTY_LINE_NUMBERS,
                msg = "There must be exactly one template command '${CommandKey.TEMPLATE_RENDERER.keyword}'. ",
            )
        }

        val fragment = templateContentParts
            .filterIsInstance<CommandContentPart>().first {
                it.keywordCommand.commandKey != CommandKey.STRIP_LINE_BEFORE_COMMENT
                        && it.keywordCommand.commandKey != CommandKey.STRIP_LINE_AFTER_COMMENT
            }

        if(!fragment.isTemplateDefinitionCommand()) {
            throw TemplateParsingException(
                lineNumbers = fragment.lineNumbers,
                msg = "The first command in a file must be '${CommandKey.TEMPLATE_RENDERER.keyword}' " +
                        "but was ${fragment.keywordCommand.commandKey.keyword}. ",
            )
        }
        return fragment.keywordCommand
    }

    private fun KeywordCommand.toModelDescription(): List<ModelDescription> {
        return this.attributeGroupIndices().map { attributeGroupIndex ->
            ModelDescription(
                modelClassDescription = this.toClassDescription(
                    groupId = attributeGroupIndex,
                    classNameAttribute = TEMPLATE_MODEL_CLASS_NAME,
                    packageNameAttribute = TEMPLATE_MODEL_PACKAGE_NAME,
                ),
                modelName = this.attribute(
                    groupId = attributeGroupIndex,
                    key = TEMPLATE_MODEL_NAME,
                ),
                isList = this.attributeOptional(
                    groupId = attributeGroupIndex,
                    key = TEMPLATE_MODEL_IS_LIST,
                )?.toBoolean() ?: false,
            )
        }
    }

    private fun KeywordCommand.toClassDescription(
        groupId: Int = 0,
        classNameAttribute: CommandAttributeKey,
        packageNameAttribute: CommandAttributeKey,
    ): ClassDescription {
        return ClassDescription(
            className = this.attribute(groupId, classNameAttribute),
            classPackageName = this.attributeOptional(groupId, packageNameAttribute) ?: DEFAULT_PACKAGE_NAME,
        )
    }

    private fun KeywordCommand.toOptionalClassDescription(
        groupId: Int = 0,
        classNameAttribute: CommandAttributeKey,
        packageNameAttribute: CommandAttributeKey,
    ): ClassDescription? {
        return this.attributeOptional(groupId, classNameAttribute)?.let { className ->
            ClassDescription(
                className = className,
                classPackageName = this.attributeOptional(groupId, packageNameAttribute) ?: DEFAULT_PACKAGE_NAME,
            )
        }
    }

    private fun TemplateContentPart.isTemplateDefinitionCommand(): Boolean {
        return this is CommandContentPart && this.keywordCommand.commandKey == CommandKey.TEMPLATE_RENDERER
    }

    private fun TemplateContentPart.isModelDefinitionCommand(): Boolean {
        return this is CommandContentPart && this.keywordCommand.commandKey == CommandKey.TEMPLATE_MODEL
    }

    private fun TemplateContentPart.isEndTemplateRendererCommand(): Boolean {
        return this is CommandContentPart && this.keywordCommand.commandKey == CommandKey.END_TEMPLATE_RENDERER
    }

}
