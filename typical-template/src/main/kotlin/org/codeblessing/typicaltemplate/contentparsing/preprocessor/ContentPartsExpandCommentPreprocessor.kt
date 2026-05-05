package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.DirectionValue
import org.codeblessing.typicaltemplate.ExpandModeValue
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TextContentPart
import org.codeblessing.typicaltemplate.toEnum

object ContentPartsExpandCommentPreprocessor {

    fun runPreprocessing(templateContentParts: List<TemplateContentPart>): List<TemplateContentPart> {
        val result = mutableListOf<TemplateContentPart>()
        val consumed = mutableSetOf<Int>()

        for (i in templateContentParts.indices) {
            if (i in consumed) continue

            val part = templateContentParts[i]
            if (part is TemplateCommentContentPart) {
                val expandCommand = part.keywordCommands.firstOrNull { it.commandKey == CommandKey.EXPAND_COMMENT }
                if (expandCommand != null) {
                    val direction = expandCommand.attribute(CommandAttributeKey.EXPAND_DIRECTION).toEnum<DirectionValue>()
                    val stripMode = expandCommand.attribute(CommandAttributeKey.STRIP_MODE).toEnum<ExpandModeValue>()
                    val processedComment = part.copy(
                        keywordCommands = part.keywordCommands.filter { it.commandKey != CommandKey.EXPAND_COMMENT }
                    )

                    if (direction == DirectionValue.FORWARD) {
                        val nextIndex = i + 1
                        if (nextIndex < templateContentParts.size && templateContentParts[nextIndex] is TextContentPart) {
                            val textPart = templateContentParts[nextIndex] as TextContentPart
                            val strippedText = stripFromStart(textPart.text, stripMode)
                            result.add(processedComment)
                            if (strippedText.isNotEmpty()) {
                                result.add(textPart.copy(text = strippedText))
                            }
                            consumed.add(nextIndex)
                            continue
                        }
                    } else if (direction == DirectionValue.BACKWARD) {
                        val prevIndex = i - 1
                        if (prevIndex >= 0 && prevIndex !in consumed && templateContentParts[prevIndex] is TextContentPart) {
                            val textPart = templateContentParts[prevIndex] as TextContentPart
                            val strippedText = stripFromEnd(textPart.text, stripMode)
                            result.removeAt(result.lastIndex)
                            if (strippedText.isNotEmpty()) {
                                result.add(textPart.copy(text = strippedText))
                            }
                            result.add(processedComment)
                            continue
                        }
                    }
                }
            }
            result.add(part)
        }

        return result
    }

    private fun stripFromStart(text: String, stripMode: ExpandModeValue): String {
        var i = 0
        while (i < text.length && (text[i] == ' ' || text[i] == '\t')) {
            i++
        }
        if (stripMode == ExpandModeValue.LINEBREAK && i < text.length) {
            if (text[i] == '\r' && i + 1 < text.length && text[i + 1] == '\n') {
                i += 2
            } else if (text[i] == '\r' || text[i] == '\n') {
                i++
            }
        }
        return text.substring(i)
    }

    private fun stripFromEnd(text: String, stripMode: ExpandModeValue): String {
        var i = text.length - 1
        while (i >= 0 && (text[i] == ' ' || text[i] == '\t')) {
            i--
        }
        if (stripMode == ExpandModeValue.LINEBREAK && i >= 0) {
            if (text[i] == '\n' && i > 0 && text[i - 1] == '\r') {
                i -= 2
            } else if (text[i] == '\n' || text[i] == '\r') {
                i--
            }
        }
        return text.substring(0, i + 1)
    }
}
