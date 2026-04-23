package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.CommentStyle
import org.codeblessing.typicaltemplate.TypicalTemplateException
import org.codeblessing.typicaltemplate.contentparsing.fragmenter.Fragmenter
import org.codeblessing.typicaltemplate.contentparsing.commandchain.CommandChainCreator
import org.codeblessing.typicaltemplate.contentparsing.commandchain.TemplateRendererDescription
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.FileContentTokenizer
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.ContentType

object ContentParser {

    fun parseContent(content: String, supportedCommentStyles: List<CommentStyle>): List<TemplateRendererDescription> {
        if(supportedCommentStyles.isEmpty()) {
            return emptyList()
        }
        try {
            val contentParts = FileContentTokenizer.tokenizeContent(content, supportedCommentStyles)

            if(contentParts.none { it.contentType == ContentType.TEMPLATE_COMMENT }) {
                // the file does not contain any typical template commands and can be ignored.
                return emptyList()
            }

            val templateFragments = Fragmenter.createFragmentsFromTokens(contentParts)
            return CommandChainCreator.validateAndInterpretFragments(templateFragments)
        } catch (ex: TemplateParsingException) {
            throw TypicalTemplateException(
                "Failed to parse at line ${ex.lineNumbers.formattedDescription}: ${ex.message}"
            )
        }
    }
}
