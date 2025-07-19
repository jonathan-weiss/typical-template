package org.codeblessing.typicaltemplate.contentparsing.tokenizer

import org.codeblessing.typicaltemplate.CommentStyle
import org.codeblessing.typicaltemplate.CommentType

object FileContentTokenizer {

    const val TT_COMMAND_LIST_START = "@@tt{{"
    const val TT_COMMAND_LIST_END = "}}tt@@"
    const val ALL_LINE_BREAKS = "\\r\\n|\\r|\\n"
    const val ALL_LINE_BREAKS_OR_END_OF_FILE = "$ALL_LINE_BREAKS|\\z"

    fun tokenizeContent(content: String, supportedCommentStyles: List<CommentStyle>): List<Token> {
        val commentPatterns = supportedCommentStyles.map { style ->
            val startOfCommentRegex = Regex.escape(style.startOfComment)
            val endOfCommentRegex = when(style.commentType) {
                CommentType.BLOCK_COMMENT -> "${Regex.escape(requireNotNull(style.endOfComment))}()"
                CommentType.LINE_COMMENT -> "(${ALL_LINE_BREAKS_OR_END_OF_FILE})"
            }

            val startOfCommandList = Regex.escape(TT_COMMAND_LIST_START)
            val endOfCommandList = Regex.escape(TT_COMMAND_LIST_END)
            "(?:$startOfCommentRegex)\\s*(?:$startOfCommandList)(.*?)(?:$endOfCommandList)\\s*(?:$endOfCommentRegex)"
        }

        val regexPattern = commentPatterns.joinToString("|") { "(?:$it)" }
        val regex = Regex(regexPattern, setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE))
        val result = mutableListOf<Token>()
        var lastIndex = 0
        var leftoverContent = ""
        for (match in regex.findAll(content)) {
            val contentBeforeCommand = if (match.range.first > lastIndex) {
                content.substring(lastIndex, match.range.first)
            } else ""

            result.addPlainContentToken(leftoverContent + contentBeforeCommand)

            val strippedCommand = match.groupValueSegment( offset = 0, numberOfGroupsPerExpression = 2)
            result.add(TemplateCommentToken(value = strippedCommand))

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

    private fun <E> cartesianProduct(list1: List<E>, list2: List<E>): List<Pair<E, E>> {
        return list1.flatMap { item1 ->
            list2.map { item2 ->
                item1 to item2
            }
        }
    }
}
