package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.DirectionValue
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TextContentPart
import org.codeblessing.typicaltemplate.toEnum

object ContentPartsMoveCommentPreprocessor {

    fun runPreprocessing(templateContentParts: List<TemplateContentPart>): List<TemplateContentPart> {
        val result = mutableListOf<TemplateContentPart>()
        val consumed = mutableSetOf<Int>()

        for (i in templateContentParts.indices) {
            if (i in consumed) continue

            val part = templateContentParts[i]
            if (part is TemplateCommentContentPart) {
                val moveCommand = part.keywordCommands.firstOrNull { it.commandKey == CommandKey.MOVE_COMMENT }
                if (moveCommand != null) {
                    val direction = moveCommand.attribute(CommandAttributeKey.DIRECTION).toEnum<DirectionValue>()

                    if (direction == DirectionValue.FORWARD) {
                        val nextIndex = i + 1
                        if (nextIndex < templateContentParts.size && templateContentParts[nextIndex] is TextContentPart) {
                            val textPart = templateContentParts[nextIndex] as TextContentPart
                            result.addAll(applyMove(part, moveCommand, textPart, direction))
                            consumed.add(nextIndex)
                            continue
                        }
                    } else if (direction == DirectionValue.BACKWARD) {
                        val prevIndex = i - 1
                        if (prevIndex >= 0 && prevIndex !in consumed && templateContentParts[prevIndex] is TextContentPart) {
                            val textPart = templateContentParts[prevIndex] as TextContentPart
                            result.removeAt(result.lastIndex)
                            result.addAll(applyMove(part, moveCommand, textPart, direction))
                            continue
                        }
                    }
                }
            }
            result.add(part)
        }

        return result
    }

    private fun applyMove(
        commentPart: TemplateCommentContentPart,
        moveCommand: KeywordCommand,
        textPart: TextContentPart,
        direction: DirectionValue,
    ): List<TemplateContentPart> {
        val processedComment = commentPart.copy(
            keywordCommands = commentPart.keywordCommands.filter { it.commandKey != CommandKey.MOVE_COMMENT }
        )

        val beforeFirstOf = moveCommand.attributeOptional(CommandAttributeKey.BEFORE_FIRST_OCCURRENCE_OF)
        val afterFirstOf = moveCommand.attributeOptional(CommandAttributeKey.AFTER_FIRST_OCCURRENCE_OF)
        val beforeLastOf = moveCommand.attributeOptional(CommandAttributeKey.BEFORE_LAST_OCCURRENCE_OF)
        val afterLastOf = moveCommand.attributeOptional(CommandAttributeKey.AFTER_LAST_OCCURRENCE_OF)

        val text = textPart.text
        val splitIndex: Int
            if(beforeFirstOf != null) {
            val idx = text.indexOf(beforeFirstOf).indexOrException(beforeFirstOf)
            splitIndex = idx
        } else if (afterFirstOf != null) {
            val idx = text.indexOf(afterFirstOf).indexOrException(afterFirstOf)
            splitIndex = idx + afterFirstOf.length
        } else if(beforeLastOf != null) {
            val idx = text.lastIndexOf(beforeLastOf).indexOrException(beforeLastOf)
            splitIndex = idx
        } else if(afterLastOf != null) {
            val idx = text.lastIndexOf(afterLastOf).indexOrException(afterLastOf)
            splitIndex = idx + afterLastOf.length
        } else {
            // all are null
            return if (direction == DirectionValue.FORWARD) listOf(textPart, processedComment) else listOf(processedComment, textPart)
        }

        val leftText = text.substring(0, splitIndex)
        val rightText = text.substring(splitIndex)

        return listOfNotNull(
            TextContentPart(textPart.lineNumbers, leftText).takeIf { leftText.isNotEmpty() },
            processedComment,
            TextContentPart(textPart.lineNumbers, rightText).takeIf { rightText.isNotEmpty() }
        )
    }

    private fun Int.indexOrException(searchToken: String): Int {
        if (this < 0) throw TemplateParsingException(
            msg = "Occurrence of '$searchToken' not found in text"
        )
        return this
    }
}
