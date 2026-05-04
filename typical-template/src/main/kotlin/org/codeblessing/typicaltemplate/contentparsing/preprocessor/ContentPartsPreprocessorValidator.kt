package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart

object ContentPartsPreprocessorValidator {

    fun validatePreprocessing(templateContentParts: List<TemplateContentPart>): List<TemplateContentPart> {
        for (part in templateContentParts) {
            if (part is TemplateCommentContentPart) {
                val moveCommentCount = part.keywordCommands.count { it.commandKey == CommandKey.MOVE_COMMENT }
                if (moveCommentCount > 1) {
                    throw TemplateParsingException(
                        lineNumbers = part.lineNumbers,
                        msg = "A template comment must not have more than one '${CommandKey.MOVE_COMMENT.keyword}' command, but found $moveCommentCount.",
                    )
                }
            }
        }
        return templateContentParts
    }
}
