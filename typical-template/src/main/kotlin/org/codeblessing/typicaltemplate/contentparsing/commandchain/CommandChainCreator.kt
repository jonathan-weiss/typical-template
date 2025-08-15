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
        val templateRendererKeywordCommand: KeywordCommand = assureFirstAndOnlyCommandIsTemplateDefinition(templateFragments)
        val templateModels = assureNoDuplicateModelNames(templateFragments)
        val templateRendererClassDescription = templateRendererKeywordCommand
            .toClassDescription(
                classNameAttribute = TEMPLATE_RENDERER_CLASS_NAME,
                packageNameAttribute = TEMPLATE_RENDERER_PACKAGE_NAME
            )


        val remainingFragments = templateFragments
            .filterNot { it.isTemplateDefinitionCommand() }
            .filterNot { it.isModelDefinitionCommand() }


        validateNestingLevelOfFragments(remainingFragments)

        val templateChainItems = adaptMutualInfluencedFragments(remainingFragments)

        val templateRendererDescription = TemplateRendererDescription(
            templateRendererClass = templateRendererClassDescription,
            modelClasses = templateModels,
            templateChain = templateChainItems
        )
        return listOf(templateRendererDescription)

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

    private fun TemplateFragment.isTemplateDefinitionCommand(): Boolean {
        return this is CommandFragment && this.keywordCommand.commandKey == CommandKey.TEMPLATE_RENDERER
    }

    private fun TemplateFragment.isModelDefinitionCommand(): Boolean {
        return this is CommandFragment && this.keywordCommand.commandKey == CommandKey.TEMPLATE_MODEL
    }

}
