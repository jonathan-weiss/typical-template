package org.codeblessing.typicaltemplate.contentparsing.fragmenter

import org.codeblessing.typicaltemplate.contentparsing.commentparser.TemplateCommentParser
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException.Companion.reThrowWithLineNumbers
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumberCalculator
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.RawContentPart
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.ContentType

/**
 * Splits the typical template comment text into a list of structured commands.
 */
object Fragmenter {

    fun createFragmentsFromTokens(contentParts: List<RawContentPart>): List<TemplateFragment> {
        return contentParts.flatMap { contentPart ->
            val lineNumbers = LineNumberCalculator.calculateLineNumbers(contentPart, contentParts)
            when (contentPart.contentType) {
                ContentType.PLAIN_TEXT -> listOf(FragmentFactory.createTextFragment(
                    text = contentPart.content,
                    lineNumbers = lineNumbers

                ))
                ContentType.TEMPLATE_COMMENT -> reThrowWithLineNumbers(lineNumbers) {
                    TemplateCommentParser.parseComment(contentPart.content)
                }
                .map { commandStructure ->
                    FragmentFactory.createCommandFragment(
                        commandStructure = commandStructure,
                        lineNumbers = lineNumbers
                    )
                }
        }
        }
    }
}
