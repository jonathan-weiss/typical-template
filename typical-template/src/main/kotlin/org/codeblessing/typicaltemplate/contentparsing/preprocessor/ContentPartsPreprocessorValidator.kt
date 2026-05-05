package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.DirectionValue
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
        val moveCommentCount = part.keywordCommands.count { it.commandKey == CommandKey.MOVE_COMMENT }
        if (moveCommentCount > 1) {
            throw TemplateParsingException(
                lineNumbers = part.lineNumbers,
                msg = "A template comment must not have more than one '${CommandKey.MOVE_COMMENT.keyword}' command, but found $moveCommentCount.",
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
                    msg = "A template comment must not have more than one '${CommandKey.EXPAND_COMMENT.keyword}' command with direction '${direction.value}', but found $count.",
                )
            }
        }
    }
}
