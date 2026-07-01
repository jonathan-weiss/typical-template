package org.codeblessing.tavnit.contentparsing.commandchain

import org.codeblessing.tavnit.CommandKey
import org.codeblessing.tavnit.contentparsing.KeywordCommand
import org.codeblessing.tavnit.contentparsing.TemplateParsingErrorCode
import org.codeblessing.tavnit.contentparsing.TemplateParsingException
import org.codeblessing.tavnit.contentparsing.linenumbers.LineNumbers
import org.codeblessing.tavnit.contentparsing.linenumbers.LineNumbers.Companion.EMPTY_LINE_NUMBERS
import org.codeblessing.tavnit.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.tavnit.contentparsing.resolver.TemplateContentPart

object KeywordCommandChainNestingHandler {

    fun validateAndHandleNestingStructure(templateContentParts: List<TemplateContentPart>): List<TemplateContentPart> {
        val result = mutableListOf<TemplateContentPart>()
        val openingCommandKeysStack = mutableListOf<CommandKey>()

        for (part in templateContentParts) {
            if (part is TemplateCommentContentPart) {
                val autoClosePartsToInsert = mutableListOf<TemplateCommentContentPart>()

                for (keywordCommand in part.keywordCommands) {
                    val commandKey = keywordCommand.commandKey

                    if (commandKey.isTriggerAutoclose) {
                        autoClosePartsToInsert.addAll(
                            collectAutocloseCommands(commandKey, openingCommandKeysStack, part.lineNumbers)
                        )
                    }

                    if (commandKey.isOpeningCommand) {
                        openingCommandKeysStack.add(commandKey)
                    } else if (commandKey.isClosingCommand) {
                        validateClosingCommand(part, keywordCommand, openingCommandKeysStack)
                        openingCommandKeysStack.removeLast()
                    }

                    if (commandKey.isRequiredDirectlyNestedInOtherCommand) {
                        validateDirectlyNestedCommand(part, keywordCommand, openingCommandKeysStack)
                    }
                }

                result.addAll(autoClosePartsToInsert)
                result.add(part)
            } else {
                result.add(part)
            }
        }

        closeRemainingOpenCommands(openingCommandKeysStack, result)

        return result
    }

    private fun collectAutocloseCommands(
        triggerCommandKey: CommandKey,
        openingCommandKeysStack: MutableList<CommandKey>,
        lineNumbers: LineNumbers,
    ): List<TemplateCommentContentPart> {
        val collected = mutableListOf<TemplateCommentContentPart>()
        val correspondingOpeningKey = requireNotNull(triggerCommandKey.correspondingOpeningCommandKeyForAutoclose)

        while (openingCommandKeysStack.isNotEmpty()) {
            val lastCommandKey = openingCommandKeysStack.last()
            if (lastCommandKey == correspondingOpeningKey) break
            if (!lastCommandKey.isAutoclosingSupported) break
            val closingKey = requireNotNull(lastCommandKey.correspondingClosingCommandKey)
            collected.add(createClosingCommandPart(closingKey, lineNumbers))
            openingCommandKeysStack.removeLast()
        }

        return collected
    }

    private fun closeRemainingOpenCommands(
        openingCommandKeysStack: MutableList<CommandKey>,
        result: MutableList<TemplateContentPart>,
    ) {
        while (openingCommandKeysStack.isNotEmpty()) {
            val commandKey = openingCommandKeysStack.last()
            if (!commandKey.isAutoclosingSupported) {
                throw TemplateParsingException(
                    lineNumbers = EMPTY_LINE_NUMBERS,
                    errorCode = TemplateParsingErrorCode.UNCLOSED_OPENING_COMMAND,
                    msg = TemplateParsingErrorCode.UNCLOSED_OPENING_COMMAND.resolve(
                        "openingCommand" to commandKey.keyword,
                        "closingCommand" to commandKey.correspondingClosingCommandKey?.keyword.toString(),
                    ),
                )
            }
            val closingKey = requireNotNull(commandKey.correspondingClosingCommandKey)
            result.add(createClosingCommandPart(closingKey, EMPTY_LINE_NUMBERS))
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
                errorCode = TemplateParsingErrorCode.MISMATCHED_CLOSING_COMMAND,
                msg = TemplateParsingErrorCode.MISMATCHED_CLOSING_COMMAND.resolve(
                    "closingCommand" to closingCommandKey.keyword,
                    "openingCommand" to correspondingOpeningCommandKey.keyword,
                ),
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
                errorCode = TemplateParsingErrorCode.COMMAND_NOT_DIRECTLY_NESTED,
                msg = TemplateParsingErrorCode.COMMAND_NOT_DIRECTLY_NESTED.resolve(
                    "command" to commandKey.keyword,
                    "enclosingCommand" to requiredEnclosingCommandKey.keyword,
                ),
            )
        }
    }

    private fun createClosingCommandPart(
        closingCommandKey: CommandKey,
        lineNumbers: LineNumbers,
    ): TemplateCommentContentPart {
        return TemplateCommentContentPart(
            lineNumbers = lineNumbers,
            keywordCommands = listOf(KeywordCommand(closingCommandKey, emptyList())),
        )
    }
}
