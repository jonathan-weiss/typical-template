package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.CommandAttributeKey.TEMPLATE_MODEL_NAME
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers.Companion.EMPTY_LINE_NUMBERS
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart

object KeywordCommandChainValidation {

    fun validate(templateContentParts: List<TemplateContentPart>) {
        val (outerFragments, nestedSections) = splitIntoSections(templateContentParts)
        validateSingleSection(outerFragments)
        for (nestedSection in nestedSections) {
            validate(nestedSection)
        }
    }

    private data class SplitResult(
        val outerFragments: List<TemplateContentPart>,
        val nestedSections: List<List<TemplateContentPart>>,
    )

    private fun splitIntoSections(templateContentParts: List<TemplateContentPart>): SplitResult {
        val outerFragments = mutableListOf<TemplateContentPart>()
        val nestedSections = mutableListOf<List<TemplateContentPart>>()
        var foundTopLevel = false
        var depth = 0
        var currentNestedSection: MutableList<TemplateContentPart>? = null

        for (fragment in templateContentParts) {
            if (currentNestedSection != null) {
                if (fragment.isTemplateDefinitionCommand()) {
                    depth++
                    currentNestedSection.add(fragment)
                } else if (fragment.isEndTemplateRendererCommand()) {
                    if (depth > 0) {
                        depth--
                        currentNestedSection.add(fragment)
                    } else {
                        nestedSections.add(currentNestedSection)
                        currentNestedSection = null
                    }
                } else {
                    currentNestedSection.add(fragment)
                }
            } else {
                if (fragment.isTemplateDefinitionCommand()) {
                    if (!foundTopLevel) {
                        foundTopLevel = true
                        outerFragments.add(fragment)
                    } else {
                        currentNestedSection = mutableListOf(fragment)
                        depth = 0
                    }
                } else if (fragment.isEndTemplateRendererCommand()) {
                    outerFragments.add(fragment)
                    val remainingIndex = templateContentParts.indexOf(fragment) + 1
                    val remainingCommands = templateContentParts
                        .subList(remainingIndex, templateContentParts.size)
                        .filterIsInstance<TemplateCommentContentPart>()
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
            val nestedRendererFragment = currentNestedSection.firstOrNull { it.isTemplateDefinitionCommand() }
            throw TemplateParsingException(
                lineNumbers = nestedRendererFragment?.lineNumbers ?: EMPTY_LINE_NUMBERS,
                msg = "Nested '${CommandKey.TEMPLATE_RENDERER.keyword}' must be closed with " +
                        "'${CommandKey.END_TEMPLATE_RENDERER.keyword}'.",
            )
        }

        return SplitResult(outerFragments, nestedSections)
    }

    private fun validateSingleSection(templateContentParts: List<TemplateContentPart>) {
        validateFirstAndOnlyCommandIsTemplateDefinition(templateContentParts)
        validateNoDuplicateModelNames(templateContentParts)
        val remainingFragments = templateContentParts
            .filterNot { it.isModelDefinitionCommand() }
        validateNestingLevelOfFragments(remainingFragments)
    }

    private fun validateFirstAndOnlyCommandIsTemplateDefinition(templateContentParts: List<TemplateContentPart>) {
        val count = templateContentParts.count { it.isTemplateDefinitionCommand() }
        if (count != 1) {
            throw TemplateParsingException(
                lineNumbers = templateContentParts.firstOrNull()?.lineNumbers ?: EMPTY_LINE_NUMBERS,
                msg = "There must be exactly one template command '${CommandKey.TEMPLATE_RENDERER.keyword}'. ",
            )
        }

        val fragment = templateContentParts
            .filterIsInstance<TemplateCommentContentPart>()
            .first { part ->
                part.keywordCommands.any {
                    it.commandKey != CommandKey.STRIP_LINE_BEFORE_COMMENT
                            && it.commandKey != CommandKey.STRIP_LINE_AFTER_COMMENT
                }
            }

        if (!fragment.isTemplateDefinitionCommand()) {
            val firstNonStripCommand = fragment.keywordCommands.first {
                it.commandKey != CommandKey.STRIP_LINE_BEFORE_COMMENT
                        && it.commandKey != CommandKey.STRIP_LINE_AFTER_COMMENT
            }
            throw TemplateParsingException(
                lineNumbers = fragment.lineNumbers,
                msg = "The first command in a file must be '${CommandKey.TEMPLATE_RENDERER.keyword}' " +
                        "but was ${firstNonStripCommand.commandKey.keyword}. ",
            )
        }
    }

    private fun validateNoDuplicateModelNames(templateContentParts: List<TemplateContentPart>) {
        val modelFragments = templateContentParts
            .filterIsInstance<TemplateCommentContentPart>()
            .filter { it.isModelDefinitionCommand() }

        val usedModelNames = mutableSetOf<String>()
        for (modelFragment in modelFragments) {
            val modelNames = modelFragment.keywordCommands
                .filter { it.commandKey == CommandKey.TEMPLATE_MODEL }
                .flatMap { command ->
                    command.attributeGroupIndices().map { groupId ->
                        command.attribute(groupId = groupId, key = TEMPLATE_MODEL_NAME)
                    }
                }
            for (modelName in modelNames) {
                if (modelName in usedModelNames) {
                    throw TemplateParsingException(
                        lineNumbers = modelFragment.lineNumbers,
                        msg = "The model name $modelName is used more than once."
                    )
                }
                usedModelNames.add(modelName)
            }
        }
    }

    private fun validateNestingLevelOfFragments(contentParts: List<TemplateContentPart>) {
        val openingCommandKeysStack: MutableList<CommandKey> = mutableListOf()

        contentParts.filterIsInstance<TemplateCommentContentPart>().forEach { commandFragment ->
            commandFragment.keywordCommands.forEach { keywordCommand ->
                val commandKey = keywordCommand.commandKey
                if (commandKey.isTriggerAutoclose) {
                    autocloseNestedStackElements(commandKey, openingCommandKeysStack)
                }
                if (commandKey.isOpeningCommand) {
                    openingCommandKeysStack.add(commandKey)
                } else if (commandKey.isClosingCommand) {
                    validateClosingCommand(commandFragment, keywordCommand, openingCommandKeysStack)
                    openingCommandKeysStack.removeLast()
                }
                if (commandKey.isRequiredDirectlyNestedInOtherCommand) {
                    validateDirectlyNestedCommand(commandFragment, keywordCommand, openingCommandKeysStack)
                }
            }
        }

        if (openingCommandKeysStack.filterNot { it.isAutoclosingSupported }.isNotEmpty()) {
            throw TemplateParsingException(
                lineNumbers = EMPTY_LINE_NUMBERS,
                msg = "The template has the following opening commands ${openingCommandKeysStack.map { it.keyword }} " +
                        "that needs to be closed using the following closing commands " +
                        "${openingCommandKeysStack.map { it.correspondingClosingCommandKey?.keyword }}."
            )
        }
    }

    private fun validateDirectlyNestedCommand(
        commandFragment: TemplateCommentContentPart,
        keywordCommand: KeywordCommand,
        openingCommandKeysStack: List<CommandKey>,
    ) {
        val commandKey = keywordCommand.commandKey
        val requiredEnclosingCommandKey = requireNotNull(commandKey.directlyNestedInsideCommandKey)
        if (openingCommandKeysStack.lastOrNull() != requiredEnclosingCommandKey) {
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
            if (lastCommandKey == correspondingOpeningCommandKey) {
                return
            }
            if (!lastCommandKey.isAutoclosingSupported) {
                return
            }
            openingCommandKeysStack.removeLast()
        }
    }

    private fun validateClosingCommand(
        commandFragment: TemplateCommentContentPart,
        keywordCommand: KeywordCommand,
        openingCommandKeysStack: List<CommandKey>,
    ) {
        val closingCommandKey = keywordCommand.commandKey
        val correspondingOpeningCommandKey = requireNotNull(closingCommandKey.correspondingOpeningCommandKey)
        val lastCommandKey = openingCommandKeysStack.lastOrNull()
        if (lastCommandKey == null || lastCommandKey != correspondingOpeningCommandKey) {
            throw TemplateParsingException(
                lineNumbers = commandFragment.lineNumbers,
                msg = "The template has a closing command '${closingCommandKey.keyword}' " +
                        "without a corresponding opening command '${correspondingOpeningCommandKey.keyword}'" +
                        "before in the template."
            )
        }
    }

    private fun TemplateContentPart.isTemplateDefinitionCommand(): Boolean {
        return this is TemplateCommentContentPart && this.keywordCommands.any { it.commandKey == CommandKey.TEMPLATE_RENDERER }
    }

    private fun TemplateContentPart.isModelDefinitionCommand(): Boolean {
        return this is TemplateCommentContentPart && this.keywordCommands.any { it.commandKey == CommandKey.TEMPLATE_MODEL }
    }

    private fun TemplateContentPart.isEndTemplateRendererCommand(): Boolean {
        return this is TemplateCommentContentPart && this.keywordCommands.any { it.commandKey == CommandKey.END_TEMPLATE_RENDERER }
    }
}
