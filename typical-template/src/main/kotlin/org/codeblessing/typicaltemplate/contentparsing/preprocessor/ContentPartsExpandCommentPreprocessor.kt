package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TextContentPart

/**
 * Removes whitespace around typical template comments.
 *
 * A comment that uses one of the explicit remove-blanks commands keeps its explicit
 * behaviour (see [BEFORE_REMOVE_COMMENT_COMMAND_KEYS] / [AFTER_REMOVE_COMMENT_COMMAND_KEYS]).
 *
 * A comment without any such command gets the default whitespace handling: the comment is
 * trimmed only when it is the only non-blank content on its line, i.e. when only blanks
 * precede it up to the start of the line and only blanks follow it up to the end of the line.
 * In that case the blanks before the comment, the blanks after the comment and the trailing
 * line break are removed, so the whole comment line collapses. If there are non-blank
 * characters before or after the comment on the same line, nothing is removed.
 *
 * The keep-blanks commands (see [KEEP_BEFORE_COMMENT_COMMAND_KEY] / [KEEP_AFTER_COMMENT_COMMAND_KEY])
 * are the counterpart of the remove-blanks commands: they suppress the default whitespace handling
 * on their side, so the blanks and the line break before respectively after the comment are kept.
 *
 * Every comment is considered on its own, looking only at the text directly before and after it.
 */
object ContentPartsExpandCommentPreprocessor {

    private val BEFORE_REMOVE_COMMENT_COMMAND_KEYS = setOf(
        CommandKey.REMOVE_BLANKS_BEFORE_COMMENT,
        CommandKey.REMOVE_BLANKS_AND_LINEBREAK_BEFORE_COMMENT,
    )
    private val AFTER_REMOVE_COMMENT_COMMAND_KEYS = setOf(
        CommandKey.REMOVE_BLANKS_AFTER_COMMENT,
        CommandKey.REMOVE_BLANKS_AND_LINEBREAK_AFTER_COMMENT,
    )
    private val REMOVE_COMMENT_COMMAND_KEYS = BEFORE_REMOVE_COMMENT_COMMAND_KEYS + AFTER_REMOVE_COMMENT_COMMAND_KEYS

    private val KEEP_BEFORE_COMMENT_COMMAND_KEY = CommandKey.KEEP_BLANKS_AND_LINEBREAK_BEFORE_COMMENT
    private val KEEP_AFTER_COMMENT_COMMAND_KEY = CommandKey.KEEP_BLANKS_AND_LINEBREAK_AFTER_COMMENT

    /** All commands that influence the whitespace handling and therefore must be stripped from the comment. */
    private val WHITESPACE_COMMENT_COMMAND_KEYS =
        REMOVE_COMMENT_COMMAND_KEYS + setOf(KEEP_BEFORE_COMMENT_COMMAND_KEY, KEEP_AFTER_COMMENT_COMMAND_KEY)

    fun runPreprocessing(templateContentParts: List<TemplateContentPart>): List<TemplateContentPart> {
        val parts = templateContentParts.toMutableList()

        var index = 0
        while (index < parts.size) {
            val part = parts[index]
            if (part !is TemplateCommentContentPart) {
                index++
                continue
            }

            if (part.hasExplicitRemoveCommand()) {
                applyExplicitRemoveCommands(parts, index)
            } else {
                applyDefaultWhitespaceHandling(parts, index)
            }
            index++
        }

        return parts.filter { it !is TextContentPart || it.text.isNotEmpty() }
    }

    private fun applyExplicitRemoveCommands(parts: MutableList<TemplateContentPart>, index: Int) {
        val comment = parts[index] as TemplateCommentContentPart
        val beforeCommand = comment.keywordCommands.firstOrNull { it.commandKey in BEFORE_REMOVE_COMMENT_COMMAND_KEYS }
        val afterCommand = comment.keywordCommands.firstOrNull { it.commandKey in AFTER_REMOVE_COMMENT_COMMAND_KEYS }

        parts[index] = comment.copy(
            keywordCommands = comment.keywordCommands.filter { it.commandKey !in WHITESPACE_COMMENT_COMMAND_KEYS }
        )

        if (beforeCommand != null) {
            val precedingIndex = index - 1
            val precedingText = parts.getOrNull(precedingIndex) as? TextContentPart
            if (precedingText != null) {
                val stripLinebreak = beforeCommand.commandKey == CommandKey.REMOVE_BLANKS_AND_LINEBREAK_BEFORE_COMMENT
                parts[precedingIndex] = precedingText.copy(text = stripFromEnd(precedingText.text, stripLinebreak))
            }
        }

        if (afterCommand != null) {
            val followingIndex = index + 1
            val followingText = parts.getOrNull(followingIndex) as? TextContentPart
            if (followingText != null) {
                val stripLinebreak = afterCommand.commandKey == CommandKey.REMOVE_BLANKS_AND_LINEBREAK_AFTER_COMMENT
                parts[followingIndex] = followingText.copy(text = stripFromStart(followingText.text, stripLinebreak))
            }
        }
    }

    /**
     * Applies the default whitespace handling to the comment at [index]: when only blanks precede
     * it up to the start of its line and only blanks follow it up to the end of its line, the
     * surrounding blanks (and the trailing line break) are removed.
     *
     * A keep-blanks command on a side suppresses this removal on that side, so the blanks and the
     * line break before respectively after the comment are kept untouched.
     */
    private fun applyDefaultWhitespaceHandling(parts: MutableList<TemplateContentPart>, index: Int) {
        val comment = parts[index] as TemplateCommentContentPart
        val keepBefore = comment.keywordCommands.any { it.commandKey == KEEP_BEFORE_COMMENT_COMMAND_KEY }
        val keepAfter = comment.keywordCommands.any { it.commandKey == KEEP_AFTER_COMMENT_COMMAND_KEY }

        parts[index] = comment.copy(
            keywordCommands = comment.keywordCommands.filter { it.commandKey !in WHITESPACE_COMMENT_COMMAND_KEYS }
        )

        val precedingIndex = index - 1
        val followingIndex = index + 1
        val precedingText = parts.getOrNull(precedingIndex) as? TextContentPart
        val followingText = parts.getOrNull(followingIndex) as? TextContentPart

        val onlyBlanksBefore = precedingText == null || onlyBlanksUpToLineStart(precedingText.text)
        val onlyBlanksAfter = followingText == null || !firstLineHasNonBlank(followingText.text)
        val isStandaloneOnLine = onlyBlanksBefore && onlyBlanksAfter
        if (isStandaloneOnLine) {
            if (precedingText != null && !keepBefore) {
                parts[precedingIndex] = precedingText.copy(text = stripFromEnd(precedingText.text, stripLinebreak = false))
            }
            if (followingText != null && !keepAfter) {
                parts[followingIndex] = followingText.copy(text = stripFromStart(followingText.text, stripLinebreak = true))
            }
        }
    }

    private fun TemplateCommentContentPart.hasExplicitRemoveCommand(): Boolean =
        keywordCommands.any { it.commandKey in REMOVE_COMMENT_COMMAND_KEYS }

    /** Whether the first line of [text] contains a non-blank character before any line break. */
    private fun firstLineHasNonBlank(text: String): Boolean {
        for (char in text) {
            if (char == '\n' || char == '\r') return false
            if (char != ' ' && char != '\t') return true
        }
        return false
    }

    /** Whether the text ends with only blanks back to the start of its last line. */
    private fun onlyBlanksUpToLineStart(text: String): Boolean {
        for (index in text.indices.reversed()) {
            val char = text[index]
            if (char == '\n' || char == '\r') return true
            if (char != ' ' && char != '\t') return false
        }
        return true
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
