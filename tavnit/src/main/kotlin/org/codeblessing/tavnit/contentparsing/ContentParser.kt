package org.codeblessing.tavnit.contentparsing

import org.codeblessing.tavnit.CommentStyle
import org.codeblessing.tavnit.TavnitException
import org.codeblessing.tavnit.contentparsing.commandchain.KeywordCommandChainCustomValidation
import org.codeblessing.tavnit.contentparsing.resolver.ContentPartResolver
import org.codeblessing.tavnit.contentparsing.commandchain.KeywordCommandChainNestingHandler
import org.codeblessing.tavnit.contentparsing.commandchain.KeywordCommandChainTemplateSplitter
import org.codeblessing.tavnit.contentparsing.commandchain.TemplateRendererDescription
import org.codeblessing.tavnit.contentparsing.preprocessor.ContentPartsExpandCommentPreprocessor
import org.codeblessing.tavnit.contentparsing.preprocessor.ContentPartsMoveCommentPreprocessor
import org.codeblessing.tavnit.contentparsing.preprocessor.ContentPartsPreprocessorValidator
import org.codeblessing.tavnit.contentparsing.preprocessor.MutuallyExclusiveCommandKeysValidator
import org.codeblessing.tavnit.contentparsing.resolver.TemplateContentPart
import org.codeblessing.tavnit.contentparsing.tokenizer.FileContentTokenizer
import org.codeblessing.tavnit.contentparsing.tokenizer.ContentType

object ContentParser {

    fun parseContent(content: String, supportedCommentStyles: List<CommentStyle>): List<TemplateRendererDescription> {
        if(supportedCommentStyles.isEmpty()) {
            return emptyList()
        }
        try {
            val rawContentParts = FileContentTokenizer.tokenizeContent(content, supportedCommentStyles)

            if(rawContentParts.none { it.contentType == ContentType.TEMPLATE_COMMENT }) {
                // the file does not contain any tavnit commands and can be ignored.
                return emptyList()
            }

            val templateContentParts = ContentPartResolver.createContentParts(rawContentParts)
                .pipe(MutuallyExclusiveCommandKeysValidator::validate)
                .pipe(ContentPartsPreprocessorValidator::validatePreprocessing)
                .pipe(ContentPartsExpandCommentPreprocessor::runPreprocessing)
                .pipe(ContentPartsMoveCommentPreprocessor::runPreprocessing)
                .pipe(KeywordCommandChainNestingHandler::validateAndHandleNestingStructure)
                .pipe( KeywordCommandChainCustomValidation::validate )
            return KeywordCommandChainTemplateSplitter.splitIntoTemplateRendererDescriptions(templateContentParts)
        } catch (ex: TemplateParsingException) {
            throw TavnitException(
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
