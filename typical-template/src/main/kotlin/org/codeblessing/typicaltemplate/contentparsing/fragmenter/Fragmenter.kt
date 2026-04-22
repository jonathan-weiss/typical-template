package org.codeblessing.typicaltemplate.contentparsing.fragmenter

import org.codeblessing.typicaltemplate.contentparsing.commentparser.TemplateCommentParser
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException.Companion.reThrowWithLineNumbers
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumberCalculator
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.PlainContentToken
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.TemplateCommentToken
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.TokenWithMetadata

/**
 * Splits the typical template comment text into a list of structured commands.
 */
object Fragmenter {

    fun createFragmentsFromTokens(tokensWithMetadata: List<TokenWithMetadata>): List<TemplateFragment> {
        return tokensWithMetadata.flatMap { tokenWithMetadata ->
            val lineNumbers = LineNumberCalculator.calculateLineNumbers(tokenWithMetadata, tokensWithMetadata)
            when (val token = tokenWithMetadata.token) {
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
