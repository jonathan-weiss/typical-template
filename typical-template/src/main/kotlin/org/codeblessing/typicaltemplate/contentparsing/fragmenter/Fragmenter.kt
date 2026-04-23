package org.codeblessing.typicaltemplate.contentparsing.fragmenter

import org.codeblessing.typicaltemplate.contentparsing.commentparser.TemplateCommentParser
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException.Companion.reThrowWithLineNumbers
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumberCalculator
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.PlainTextContentPart
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.ContentPartWithMetadata

/**
 * Splits the typical template comment text into a list of structured commands.
 */
object Fragmenter {

    fun createFragmentsFromTokens(contentParts: List<ContentPartWithMetadata>): List<TemplateFragment> {
        return contentParts.flatMap { contentPart ->
            val lineNumbers = LineNumberCalculator.calculateLineNumbers(contentPart, contentParts)
            when (val contentPart = contentPart.contentPart) {
                is PlainTextContentPart -> listOf(FragmentFactory.createTextFragment(
                    text = contentPart.value,
                    lineNumbers = lineNumbers

                ))
                is TemplateCommentContentPart -> reThrowWithLineNumbers(lineNumbers) {
                    TemplateCommentParser.parseComment(contentPart.value)
                }
                .map { structuredKeyword ->
                    FragmentFactory.createCommandFragment(
                        structuredKeyword = structuredKeyword,
                        lineNumbers = lineNumbers
                    )
                }
        }
        }
    }
}
