package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TextContentPart

/**
 * Removes whitespace around typical template comments.
 *
 * The before side and the after side of a comment are handled independently. The processing
 * follows these steps for every comment:
 *
 * 1. Decide once whether the default whitespace behaviour applies at all: it applies only when
 *    the comment is the only non-blank content on its line, i.e. when only blanks precede it up
 *    to the start of the line and only blanks follow it up to the end of the line.
 * 2. From that, derive a default decision per side. When the default applies, the blanks before
 *    the comment are removed (the preceding line break is kept) and the blanks together with the
 *    trailing line break after the comment are removed, so the whole comment line collapses.
 *    When the default does not apply, nothing is removed on either side.
 * 3. Each side is then processed on its own.
 * 4. A remove-blanks command overrides the default decision for its own side only (and never
 *    touches the other side).
 * 5. A keep-blanks command overrides the default decision for its own side only, keeping the
 *    blanks and the line break on that side.
 *
 * Conflicting keep/remove commands on the same side cannot occur (they are rejected earlier by
 * the validators), and a command that does the same thing as the default decision is simply a
 * no-op. Every comment is considered on its own, looking only at the text directly before and
 * after it.
 */
object ContentPartsExpandCommentPreprocessor {

    private enum class WhitespaceAction { KEEP, STRIP_BLANKS, STRIP_BLANKS_AND_LINEBREAK }

    private val BEFORE_COMMAND_ACTIONS = mapOf(
        CommandKey.REMOVE_BLANKS_BEFORE_COMMENT to WhitespaceAction.STRIP_BLANKS,
        CommandKey.REMOVE_BLANKS_AND_LINEBREAK_BEFORE_COMMENT to WhitespaceAction.STRIP_BLANKS_AND_LINEBREAK,
        CommandKey.KEEP_BLANKS_AND_LINEBREAK_BEFORE_COMMENT to WhitespaceAction.KEEP,
    )
    private val AFTER_COMMAND_ACTIONS = mapOf(
        CommandKey.REMOVE_BLANKS_AFTER_COMMENT to WhitespaceAction.STRIP_BLANKS,
        CommandKey.REMOVE_BLANKS_AND_LINEBREAK_AFTER_COMMENT to WhitespaceAction.STRIP_BLANKS_AND_LINEBREAK,
        CommandKey.KEEP_BLANKS_AND_LINEBREAK_AFTER_COMMENT to WhitespaceAction.KEEP,
    )

    /** All commands that influence the whitespace handling and therefore must be stripped from the comment. */
    private val WHITESPACE_COMMENT_COMMAND_KEYS = BEFORE_COMMAND_ACTIONS.keys + AFTER_COMMAND_ACTIONS.keys

    fun runPreprocessing(templateContentParts: List<TemplateContentPart>): List<TemplateContentPart> {
        val parts = templateContentParts.toMutableList()

        var index = 0
        while (index < parts.size) {
            if (parts[index] is TemplateCommentContentPart) {
                applyWhitespaceHandling(parts, index)
            }
            index++
        }

        return parts.filter { it !is TextContentPart || it.text.isNotEmpty() }
    }

    private fun applyWhitespaceHandling(parts: MutableList<TemplateContentPart>, index: Int) {
        val comment = parts[index] as TemplateCommentContentPart
        val precedingIndex = index - 1
        val followingIndex = index + 1
        val precedingText = parts.getOrNull(precedingIndex) as? TextContentPart
        val followingText = parts.getOrNull(followingIndex) as? TextContentPart

        // Step 1: does the default whitespace behaviour apply at all?
        val onlyBlanksBefore = precedingText == null || onlyBlanksUpToLineStart(precedingText.text)
        val onlyBlanksAfter = followingText == null || !firstLineHasNonBlank(followingText.text)
        val isStandaloneOnLine = onlyBlanksBefore && onlyBlanksAfter

        // Step 2: default decision per side (only meaningful when standalone)
        val beforeDefault = if (isStandaloneOnLine) WhitespaceAction.STRIP_BLANKS else WhitespaceAction.KEEP
        val afterDefault = if (isStandaloneOnLine) WhitespaceAction.STRIP_BLANKS_AND_LINEBREAK else WhitespaceAction.KEEP

        // Steps 3-5: a command overrides the default for its own side only
        val beforeAction = comment.commandAction(BEFORE_COMMAND_ACTIONS) ?: beforeDefault
        val afterAction = comment.commandAction(AFTER_COMMAND_ACTIONS) ?: afterDefault

        parts[index] = comment.copy(
            keywordCommands = comment.keywordCommands.filter { it.commandKey !in WHITESPACE_COMMENT_COMMAND_KEYS }
        )

        if (precedingText != null && beforeAction != WhitespaceAction.KEEP) {
            parts[precedingIndex] = precedingText.copy(
                text = stripFromEnd(precedingText.text, beforeAction == WhitespaceAction.STRIP_BLANKS_AND_LINEBREAK)
            )
        }
        if (followingText != null && afterAction != WhitespaceAction.KEEP) {
            parts[followingIndex] = followingText.copy(
                text = stripFromStart(followingText.text, afterAction == WhitespaceAction.STRIP_BLANKS_AND_LINEBREAK)
            )
        }
    }

    private fun TemplateCommentContentPart.commandAction(actions: Map<CommandKey, WhitespaceAction>): WhitespaceAction? =
        keywordCommands.firstNotNullOfOrNull { actions[it.commandKey] }

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
