package org.codeblessing.typicaltemplate.contentparsing

object FileContentTokenizer {

    const val IGNORE_LINE_BEFORE_MARKER = "@#ignore-line-before"
    const val IGNORE_LINE_AFTER_MARKER = "@#ignore-line-after"
    const val TT_COMMAND_MARKER = "@@tt-"

    sealed interface Token {
        val value: String
    }

    data class PlainContentToken(
        override val value: String,
    ) : Token

    data class TemplateCommentToken(
        override val value: String,
    ) : Token

    private fun <E> cartesianProduct(list1: List<E>, list2: List<E>): List<Pair<E, E>> {
        return list1.flatMap { item1 ->
            list2.map { item2 ->
                item1 to item2
            }
        }
    }

    fun tokenizeContent(content: String, supportedCommentStyles: List<CommentStyle>): List<Token> {
        val ttCommandMarkerEscaped = Regex.escape(TT_COMMAND_MARKER)
        val ignoreLineBeforeMarkerEscaped = Regex.escape(IGNORE_LINE_BEFORE_MARKER)
        val ignoreLineAfterMarkerEscaped = Regex.escape(IGNORE_LINE_AFTER_MARKER)
        val commentPatterns = supportedCommentStyles.flatMap { style ->
            val startEscaped = "(?:${style.startOfCommentRegex})"

            val startOfCommentRegexes = listOf(
                "(?:^.*)$startEscaped\\s*$ignoreLineBeforeMarkerEscaped\\s*",
                "(?:)$startEscaped\\s*",
            )
            val endEscaped = "(?:${style.endOfCommentRegex})"
            val endCommentRegex = if(style.includeEndCommentInContent) "($endEscaped)" else "()$endEscaped"
            val endOfCommentRegexes = listOf(
                "\\s*$ignoreLineAfterMarkerEscaped\\s*$endCommentRegex(?:.*$)",
                "\\s*$endCommentRegex(?:)",
            )

            cartesianProduct(startOfCommentRegexes, endOfCommentRegexes).map { (beforeRegex, afterRegex) ->
                "$beforeRegex((?:$ttCommandMarkerEscaped).*?)$afterRegex"
            }
        }

        val regexPattern = commentPatterns.joinToString("|")
        val regex = Regex(regexPattern, RegexOption.DOT_MATCHES_ALL)
        val result = mutableListOf<Token>()
        var lastIndex = 0
        var leftoverContent = ""
        for (match in regex.findAll(content)) {
            val contentBeforeCommand = if (match.range.first > lastIndex) {
                content.substring(lastIndex, match.range.first)
            } else ""

            result.addPlainContentToken(leftoverContent + contentBeforeCommand)

            val strippedCommand = match.groupValueSegment( offset = 0, numberOfGroupsPerExpression = 2)
            result.add(TemplateCommentToken(strippedCommand))

            leftoverContent = match.groupValueSegment(offset = 1, numberOfGroupsPerExpression = 2)
            lastIndex = match.range.last + 1
        }


        if (leftoverContent.isNotEmpty() || lastIndex < content.length) {
            result.addPlainContentToken(leftoverContent + content.substring(lastIndex))
        }
        return result
    }

    private fun MatchResult.groupValueSegment(
        offset: Int,
        numberOfGroupsPerExpression: Int,
    ): String {
        var content = ""
        for (index in 1 + offset until this.groupValues.size step numberOfGroupsPerExpression) {
            content += this.groupValues[index]

        }
        return content
    }

    private fun MutableList<Token>.addPlainContentToken(content: String) {
        if(content.isNotEmpty()) {
            add(PlainContentToken(content))
        }
    }
}
