package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.CommentStyle
import org.codeblessing.typicaltemplate.TypicalTemplateException
import org.codeblessing.typicaltemplate.contentparsing.commandchain.KeywordCommandChainCustomValidation
import org.codeblessing.typicaltemplate.contentparsing.resolver.ContentPartResolver
import org.codeblessing.typicaltemplate.contentparsing.commandchain.KeywordCommandChainNestingHandler
import org.codeblessing.typicaltemplate.contentparsing.commandchain.KeywordCommandChainTemplateSplitter
import org.codeblessing.typicaltemplate.contentparsing.commandchain.TemplateRendererDescription
import org.codeblessing.typicaltemplate.contentparsing.preprocessor.ContentPartsPreprocessor
import org.codeblessing.typicaltemplate.contentparsing.preprocessor.ContentPartsPreprocessorValidator
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.FileContentTokenizer
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.ContentType

object ContentParser {

    fun parseContent(content: String, supportedCommentStyles: List<CommentStyle>): List<TemplateRendererDescription> {
        if(supportedCommentStyles.isEmpty()) {
            return emptyList()
        }
        try {
            val rawContentParts = FileContentTokenizer.tokenizeContent(content, supportedCommentStyles)

            if(rawContentParts.none { it.contentType == ContentType.TEMPLATE_COMMENT }) {
                // the file does not contain any typical template commands and can be ignored.
                return emptyList()
            }

            val templateContentParts = ContentPartResolver.createContentParts(rawContentParts)
                .pipe(ContentPartsPreprocessorValidator::validatePreprocessing)
                .pipe(ContentPartsPreprocessor::runPreprocessing)
                .pipe(KeywordCommandChainNestingHandler::validateAndHandleNestingStructure)
                .pipe( KeywordCommandChainCustomValidation::validate )
            return KeywordCommandChainTemplateSplitter.splitIntoTemplateRendererDescriptions(templateContentParts)
        } catch (ex: TemplateParsingException) {
            throw TypicalTemplateException(
                "Failed to parse at line ${ex.lineNumbers.formattedDescription}: ${ex.message}"
            )
        }
    }

    private fun List<TemplateContentPart>.pipe(
        function: (templateContentParts: List<TemplateContentPart>) -> List<TemplateContentPart>,
    ): List<TemplateContentPart> {
        return function(this)
    }
}
