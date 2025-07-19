package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.CommentStyle
import org.codeblessing.typicaltemplate.TypicalTemplateException
import org.codeblessing.typicaltemplate.contentparsing.fragmenter.Fragmenter
import org.codeblessing.typicaltemplate.contentparsing.commandchain.CommandChainCreator
import org.codeblessing.typicaltemplate.contentparsing.commandchain.TemplateRenderer
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.FileContentTokenizer
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.TemplateCommentToken

object ContentParser {

    fun parseContent(content: String, supportedCommentStyles: List<CommentStyle>): List<TemplateRenderer> {
        try {
            // TODO Tokenizer darf keine Zeilen/Zeichen verlieren, alles muss drin sein
            // TODO Tokenizer muss mehrere Befehle unterstützen
            val tokens = FileContentTokenizer.tokenizeContent(content, supportedCommentStyles)

            if(tokens.none { it is TemplateCommentToken }) {
                // the file does not contain any typical template commands and can be ignored.
                return emptyList()
            }

            val templateFragments = Fragmenter.createFragmentsFromTokens(tokens)
            return CommandChainCreator.validateAndInterpretFragments(templateFragments)
        } catch (ex: TemplateParsingException) {
            throw TypicalTemplateException(
                "Failed to parse at line ${ex.lineNumbers.formattedDescription}: ${ex.message}"
            )
        }
    }
}
