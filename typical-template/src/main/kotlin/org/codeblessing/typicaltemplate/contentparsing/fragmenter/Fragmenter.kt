package org.codeblessing.typicaltemplate.contentparsing.fragmenter

import org.codeblessing.typicaltemplate.contentparsing.commentparser.TemplateCommentParser
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException.Companion.reThrowWithLineNumbers
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumberCalculator
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.PlainContentToken
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.TemplateCommentToken
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.TokenWithMetadata

object Fragmenter {

    fun createFragmentsFromTokens(tokensWithMetadata: List<TokenWithMetadata>): List<TemplateFragment> {
        return tokensWithMetadata.flatMap { tokenWithMetadata ->
            val lineNumbers = LineNumberCalculator.calculateLineNumbers(tokenWithMetadata, tokensWithMetadata)
            val token = tokenWithMetadata.token
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
