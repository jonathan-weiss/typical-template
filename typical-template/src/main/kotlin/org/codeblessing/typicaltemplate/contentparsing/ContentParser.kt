package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.TypicalTemplateException
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException.Companion.reThrowWithLineNumbers

object ContentParser {

    fun parseContent(content: String, supportedCommentStyles: List<CommentStyle>): List<TemplateRenderer> {
        try {
            val tokens = FileContentTokenizer.tokenizeContent(content, supportedCommentStyles)

            if(tokens.none { it is FileContentTokenizer.TemplateCommentToken }) {
                // the file does not contain any typical template commands and can be ignored.
                return emptyList()
            }

            val templateFragments = tokens.map { token ->
                val lineNumbers = LineNumberCalculator.calculateLineNumbers(token, tokens)
                when (token) {
                    is FileContentTokenizer.PlainContentToken -> FragmentFactory.createTextFragment(
                        text = token.value,
                        lineNumbers = lineNumbers

                    )
                    is FileContentTokenizer.TemplateCommentToken -> FragmentFactory.createCommandFragment(
                        templateComment = reThrowWithLineNumbers(lineNumbers) {
                            TemplateCommentParser.parseComment(token.value)
                        },
                        lineNumbers = lineNumbers
                    )
                }
            }
            return CommandChainValidator.validateCommands(templateFragments)
        } catch (ex: TemplateParsingException) {
            throw TypicalTemplateException(
                "Failed to parse at line ${ex.lineNumbers.formattedDescription}: ${ex.message}"
            )
        }
    }
}
