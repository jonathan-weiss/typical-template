package org.codeblessing.typicaltemplate.contentparsing.fragmenter

import org.codeblessing.typicaltemplate.contentparsing.commentparser.TemplateCommentParser
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException.Companion.reThrowWithLineNumbers
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumberCalculator
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.PlainContentToken
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.TemplateCommentToken
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.Token

object Fragmenter {

    fun createFragmentsFromTokens(tokens: List<Token>): List<TemplateFragment> {
        return tokens.flatMap { token ->
            val lineNumbers = LineNumberCalculator.calculateLineNumbers(token, tokens)
            when (token) {
                is PlainContentToken -> listOf(FragmentFactory.createTextFragment(
                    text = token.value,
                    lineNumbers = lineNumbers

                ))
                is TemplateCommentToken -> reThrowWithLineNumbers(lineNumbers) {
                    TemplateCommentParser.parseComment(token.value)
                }
                .map { templateComment ->
                    FragmentFactory.createCommandFragment(
                        structuredComment = templateComment,
                        lineNumbers = lineNumbers
                    )
                }
        }
        }
    }
}
