package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TextContentPart

object ContentPartsExpandCommentPreprocessor {

    private val REMOVE_COMMENT_COMMAND_KEYS = setOf(
        CommandKey.REMOVE_BLANKS_BEFORE_COMMENT,
        CommandKey.REMOVE_BLANKS_AFTER_COMMENT,
        CommandKey.REMOVE_BLANKS_AND_LINEBREAK_BEFORE_COMMENT,
        CommandKey.REMOVE_BLANKS_AND_LINEBREAK_AFTER_COMMENT,
    )

    fun runPreprocessing(templateContentParts: List<TemplateContentPart>): List<TemplateContentPart> {
        val result = mutableListOf<TemplateContentPart>()
        val consumed = mutableSetOf<Int>()

        for (i in templateContentParts.indices) {
            if (i in consumed) continue

            val part = templateContentParts[i]
            if (part is TemplateCommentContentPart) {
                val beforeCommand = part.keywordCommands.firstOrNull {
                    it.commandKey == CommandKey.REMOVE_BLANKS_BEFORE_COMMENT
                            || it.commandKey == CommandKey.REMOVE_BLANKS_AND_LINEBREAK_BEFORE_COMMENT
                }
                val afterCommand = part.keywordCommands.firstOrNull {
                    it.commandKey == CommandKey.REMOVE_BLANKS_AFTER_COMMENT
                            || it.commandKey == CommandKey.REMOVE_BLANKS_AND_LINEBREAK_AFTER_COMMENT
                }
                if (beforeCommand != null || afterCommand != null) {
                    val processedComment = part.copy(
                        keywordCommands = part.keywordCommands.filter { it.commandKey !in REMOVE_COMMENT_COMMAND_KEYS }
                    )

                    if (beforeCommand != null) {
                        val prevIndex = i - 1
                        if (prevIndex >= 0 && prevIndex !in consumed && templateContentParts[prevIndex] is TextContentPart) {
                            val textPart = templateContentParts[prevIndex] as TextContentPart
                            val stripLinebreak = beforeCommand.commandKey == CommandKey.REMOVE_BLANKS_AND_LINEBREAK_BEFORE_COMMENT
                            val strippedText = stripFromEnd(textPart.text, stripLinebreak)
                            result.removeAt(result.lastIndex)
                            if (strippedText.isNotEmpty()) {
                                result.add(textPart.copy(text = strippedText))
                            }
                        }
                    }

                    result.add(processedComment)

                    if (afterCommand != null) {
                        val nextIndex = i + 1
                        if (nextIndex < templateContentParts.size && templateContentParts[nextIndex] is TextContentPart) {
                            val textPart = templateContentParts[nextIndex] as TextContentPart
                            val stripLinebreak = afterCommand.commandKey == CommandKey.REMOVE_BLANKS_AND_LINEBREAK_AFTER_COMMENT
                            val strippedText = stripFromStart(textPart.text, stripLinebreak)
                            if (strippedText.isNotEmpty()) {
                                result.add(textPart.copy(text = strippedText))
                            }
                            consumed.add(nextIndex)
                        }
                    }
                    continue
                }
            }
            result.add(part)
        }

        return result
    }

    private fun stripFromStart(text: String, stripLinebreak: Boolean): String {
        var i = 0
        while (i < text.length && (text[i] == ' ' || text[i] == '\t')) {
            i++
        }
        if (stripLinebreak && i < text.length) {
            if (text[i] == '\r' && i + 1 < text.length && text[i + 1] == '\n') {
                i += 2
            } else if (text[i] == '\r' || text[i] == '\n') {
                i++
            }
        }
        return text.substring(i)
    }

    private fun stripFromEnd(text: String, stripLinebreak: Boolean): String {
        var i = text.length - 1
        while (i >= 0 && (text[i] == ' ' || text[i] == '\t')) {
            i--
        }
        if (stripLinebreak && i >= 0) {
            if (text[i] == '\n' && i > 0 && text[i - 1] == '\r') {
                i -= 2
            } else if (text[i] == '\n' || text[i] == '\r') {
                i--
            }
        }
        return text.substring(0, i + 1)
    }
}
