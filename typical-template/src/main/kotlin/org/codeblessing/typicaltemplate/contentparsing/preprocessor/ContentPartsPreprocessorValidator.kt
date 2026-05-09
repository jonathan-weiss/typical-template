package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.DirectionValue
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingErrorCode
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart

object ContentPartsPreprocessorValidator {

    fun validatePreprocessing(templateContentParts: List<TemplateContentPart>): List<TemplateContentPart> {
        for (part in templateContentParts) {
            if (part is TemplateCommentContentPart) {
                validateMoveComment(part)
                validateExpandComment(part)
            }
        }
        return templateContentParts
    }

    private fun validateMoveComment(part: TemplateCommentContentPart) {
        val moveCommentCount = part.keywordCommands.count {
            it.commandKey == CommandKey.MOVE_COMMENT_FORWARD || it.commandKey == CommandKey.MOVE_COMMENT_BACKWARD
        }
        if (moveCommentCount > 1) {
            throw TemplateParsingException(
                lineNumbers = part.lineNumbers,
                errorCode = TemplateParsingErrorCode.MULTIPLE_MOVE_COMMENT_COMMANDS,
                msg = TemplateParsingErrorCode.MULTIPLE_MOVE_COMMENT_COMMANDS.resolve(
                    "count" to moveCommentCount.toString(),
                ),
            )
        }
    }

    private fun validateExpandComment(part: TemplateCommentContentPart) {
        val expandCommentCommands = part.keywordCommands.filter { it.commandKey == CommandKey.EXPAND_COMMENT }
        for (direction in DirectionValue.entries) {
            val count = expandCommentCommands.count {
                it.attribute(CommandAttributeKey.EXPAND_DIRECTION) == direction.value
            }
            if (count > 1) {
                throw TemplateParsingException(
                    lineNumbers = part.lineNumbers,
                    errorCode = TemplateParsingErrorCode.MULTIPLE_EXPAND_COMMENT_COMMANDS,
                    msg = TemplateParsingErrorCode.MULTIPLE_EXPAND_COMMENT_COMMANDS.resolve(
                        "command" to CommandKey.EXPAND_COMMENT.keyword,
                        "direction" to direction.value,
                        "count" to count.toString(),
                    ),
                )
            }
        }
    }
}
