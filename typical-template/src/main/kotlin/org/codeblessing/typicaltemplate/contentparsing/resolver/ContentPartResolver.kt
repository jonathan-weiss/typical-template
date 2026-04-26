package org.codeblessing.typicaltemplate.contentparsing.resolver

import org.codeblessing.typicaltemplate.contentparsing.commentparser.TemplateCommentParser
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException.Companion.reThrowWithLineNumbers
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumberCalculator
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.RawContentPart
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.ContentType

/**
 * Splits the typical template comment text into a list of structured commands.
 */
object ContentPartResolver {

    fun createContentParts(contentParts: List<RawContentPart>): List<TemplateContentPart> {
        return contentParts.flatMap { contentPart ->
            val lineNumbers = LineNumberCalculator.calculateLineNumbers(contentPart, contentParts)
            when (contentPart.contentType) {
                ContentType.PLAIN_TEXT -> listOf(TextContentPart(lineNumbers = lineNumbers, text = contentPart.content)
)
                ContentType.TEMPLATE_COMMENT -> reThrowWithLineNumbers(lineNumbers) {
                    TemplateCommentParser.parseComment(contentPart.content)
                }
                .map { commandStructure ->
                    CommandContentPart(
                        lineNumbers = lineNumbers,
                        keywordCommand = KeywordCommandFactory.createKeywordCommand(
                            commandStructure = commandStructure,
                            lineNumbers = lineNumbers
                        )
                    )
                }
        }
        }
    }
}
