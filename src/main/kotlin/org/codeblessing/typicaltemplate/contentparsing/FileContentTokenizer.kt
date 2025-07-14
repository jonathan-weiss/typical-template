package org.codeblessing.typicaltemplate.contentparsing

object FileContentTokenizer {

    const val START_COMMAND_PREFIX = "@@tt-"
    const val END_COMMAND_PREFIX = "@@\$tt-"

    sealed interface Token {
        val value: String
    }

    data class PlainContentToken(
        override val value: String,
    ) : Token

    data class TemplateCommentToken(
        override val value: String,
    ) : Token

    fun tokenizeContent(content: String, supportedCommentStyles: List<CommentStyle>): List<Token> {
        val startCommandPrefixEscaped = Regex.escape(START_COMMAND_PREFIX)
        val endCommandPrefixEscaped = Regex.escape(END_COMMAND_PREFIX)
        val commentPatterns = supportedCommentStyles.map { style ->
                val startEscaped = Regex.escape(style.startOfComment)
                val endEscaped = Regex.escape(style.endOfComment)
                "$startEscaped\\s*(($startCommandPrefixEscaped|$endCommandPrefixEscaped).*?)\\s*$endEscaped"
        }
        val regexPattern = commentPatterns.joinToString("|")
        val regex = Regex(regexPattern, RegexOption.DOT_MATCHES_ALL)
        val result = mutableListOf<Token>()
        var lastIndex = 0
        for (match in regex.findAll(content)) {
            if (match.range.first > lastIndex) {
                result.add(PlainContentToken(content.substring(lastIndex, match.range.first)))
            }
            val stripped = match.groupValues[1]
            result.add(TemplateCommentToken(stripped))
            lastIndex = match.range.last + 1
        }
        if (lastIndex < content.length) {
            result.add(PlainContentToken(content.substring(lastIndex)))
        }
        return result
    }
}
