package org.codeblessing.typicaltemplate.contentparsing.fragmenter

import org.codeblessing.typicaltemplate.contentparsing.commentparser.TemplateCommentParser
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException.Companion.reThrowWithLineNumbers
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumberCalculator
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.PlainContentToken
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.TemplateCommentToken
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.Token

object Fragmenter {

    fun createFragmentsFromTokens(tokens: List<Token>): List<TemplateFragment> {
        return tokens.map { token ->
            val lineNumbers = LineNumberCalculator.calculateLineNumbers(token, tokens)
            when (token) {
                is PlainContentToken -> FragmentFactory.createTextFragment(
                    text = token.value,
                    lineNumbers = lineNumbers

                )
                is TemplateCommentToken -> FragmentFactory.createCommandFragment(
                    templateComment = reThrowWithLineNumbers(lineNumbers) {
                        TemplateCommentParser.parseComment(token.value)
                    },
                    lineNumbers = lineNumbers
                )
            }
        }
    }
}
