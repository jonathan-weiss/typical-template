package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandAttributeKey.*
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.fragmenter.CommandFragment
import org.codeblessing.typicaltemplate.contentparsing.fragmenter.TemplateFragment
import org.codeblessing.typicaltemplate.contentparsing.fragmenter.TextFragment
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers.Companion.EMPTY_LINE_NUMBERS

object CommandChainCreator {

    private const val DEFAULT_PACKAGE_NAME = ""

    fun validateAndInterpretFragments(templateFragments: List<TemplateFragment>): List<TemplateRendererDescription> {
        val (outerFragments, nestedSections) = splitNestedTemplateRenderers(templateFragments)
        val result = mutableListOf<TemplateRendererDescription>()
        result.add(validateAndInterpretSingleTemplate(outerFragments))
        for (nestedSection in nestedSections) {
            result.addAll(validateAndInterpretFragments(nestedSection))
        }
        return result
    }

    private fun validateAndInterpretSingleTemplate(templateFragments: List<TemplateFragment>): TemplateRendererDescription {
        val templateRendererKeywordCommand: KeywordCommand = assureFirstAndOnlyCommandIsTemplateDefinition(templateFragments)
        val templateModels = assureNoDuplicateModelNames(templateFragments)
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

        val remainingFragments = templateFragments
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
        val outerFragments: List<TemplateFragment>,
        val nestedSections: List<List<TemplateFragment>>,
    )

    private fun splitNestedTemplateRenderers(templateFragments: List<TemplateFragment>): SplitResult {
        val outerFragments = mutableListOf<TemplateFragment>()
        val nestedSections = mutableListOf<List<TemplateFragment>>()
        var foundTopLevel = false
        var depth = 0
        var currentNestedSection: MutableList<TemplateFragment>? = null

        for (fragment in templateFragments) {
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
                    val remainingIndex = templateFragments.indexOf(fragment) + 1
                    val remainingFragments = templateFragments.subList(remainingIndex, templateFragments.size)
                    val remainingCommands = remainingFragments.filterIsInstance<CommandFragment>()
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

    private fun validateNestingLevelOfFragments(fragments: List<TemplateFragment>) {
        val openingCommandKeysStack: MutableList<CommandKey> = mutableListOf()

        fragments.filterIsInstance<CommandFragment>().forEach { commandFragment ->
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
        commandFragment: CommandFragment,
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
        commandFragment: CommandFragment,
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

    private fun adaptMutualInfluencedFragments(fragments: List<TemplateFragment>): List<ChainItem> {
        val templateChainItems = mutableListOf<ChainItem>()
        fragments.forEachIndexed { index, templateFragment ->
            when (templateFragment) {
                is CommandFragment -> {
                    when (templateFragment.keywordCommand.commandKey) {
                        CommandKey.STRIP_LINE_BEFORE_COMMENT,
                        CommandKey.STRIP_LINE_AFTER_COMMENT,
                             -> Unit
                        else -> templateChainItems.add(CommandChainItem(keywordCommand = templateFragment.keywordCommand))
                    }
                }
                is TextFragment -> {
                    templateChainItems.add(createPlainTextChainItem(templateFragment, index, fragments))
                }
            }
        }
        return templateChainItems
    }

    private fun createPlainTextChainItem(
        templateFragment: TextFragment,
        index: Int,
        fragments: List<TemplateFragment>
    ): PlainTextChainItem {
        val hasPrecedingStripLineAfterComment = fragments
            .subList(0, index)
            .reversed()
            .hasAnyFollowingCommandBeforeNextText(CommandKey.STRIP_LINE_AFTER_COMMENT)

        val hasFollowingStripLineBeforeComment = fragments
            .subList(index + 1, fragments.size)
            .hasAnyFollowingCommandBeforeNextText(CommandKey.STRIP_LINE_BEFORE_COMMENT)

        return PlainTextChainItem(
            text = templateFragment.text,
            removeFirstLineIfWhitespaces = hasPrecedingStripLineAfterComment,
            removeLastLineIfWhitespaces = hasFollowingStripLineBeforeComment,
        )
    }

    private fun List<TemplateFragment>.hasAnyFollowingCommandBeforeNextText(commandKey: CommandKey): Boolean {
        return this.takeWhile { it !is TextFragment }
            .filterIsInstance<CommandFragment>()
            .any { it.keywordCommand.commandKey == commandKey }
    }

    private fun assureNoDuplicateModelNames(
        templateFragments: List<TemplateFragment>
    ): List<ModelDescription> {

        val modelFragments = templateFragments
            .filterIsInstance<CommandFragment>()
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
        templateFragments: List<TemplateFragment>
    ): KeywordCommand {
        val count = templateFragments.count { it.isTemplateDefinitionCommand() }

        if(count != 1) {
            throw TemplateParsingException(
                lineNumbers = templateFragments.firstOrNull()?.lineNumbers ?: EMPTY_LINE_NUMBERS,
                msg = "There must be exactly one template command '${CommandKey.TEMPLATE_RENDERER.keyword}'. ",
            )
        }

        val fragment = templateFragments
            .filterIsInstance<CommandFragment>().first {
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

    private fun TemplateFragment.isTemplateDefinitionCommand(): Boolean {
        return this is CommandFragment && this.keywordCommand.commandKey == CommandKey.TEMPLATE_RENDERER
    }

    private fun TemplateFragment.isModelDefinitionCommand(): Boolean {
        return this is CommandFragment && this.keywordCommand.commandKey == CommandKey.TEMPLATE_MODEL
    }

    private fun TemplateFragment.isEndTemplateRendererCommand(): Boolean {
        return this is CommandFragment && this.keywordCommand.commandKey == CommandKey.END_TEMPLATE_RENDERER
    }

}
