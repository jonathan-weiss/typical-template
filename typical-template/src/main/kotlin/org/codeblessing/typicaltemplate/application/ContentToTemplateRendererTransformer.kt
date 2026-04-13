package org.codeblessing.typicaltemplate.application

import org.codeblessing.typicaltemplate.CommentStyle
import org.codeblessing.typicaltemplate.RelativeFile
import org.codeblessing.typicaltemplate.contentparsing.ContentParser
import org.codeblessing.typicaltemplate.templaterenderer.TemplateRendererClassContentCreator
import org.codeblessing.typicaltemplate.templaterenderer.TemplateRendererContentCreator
import java.nio.file.Path

object ContentToTemplateRendererTransformer {

    fun parseContentAndCreateTemplateRenderers(
        filepath: RelativeFile,
        contentToParse: String,
        supportedCommentStyles: List<CommentStyle>,
        targetBasePath: Path
    ): List<TemplateRendererClass> {
        val templates = ContentParser.parseContent(contentToParse, supportedCommentStyles)
        return templates.map { templateRendererDescription ->
            val kotlinTemplateContent = TemplateRendererContentCreator.createMultilineStringTemplateContent(filepath, templateRendererDescription)
            val kotlinTemplateRendererClassContent = TemplateRendererClassContentCreator.wrapInKotlinClassContent(templateRendererDescription, kotlinTemplateContent)
            val kotlinFilePath = templateRendererDescription.templateRendererClass.classFilePath(targetBasePath)

            TemplateRendererClass(
                templateRendererDescription = templateRendererDescription,
                templateRendererClassContent = kotlinTemplateRendererClassContent,
                templateRendererClassFilePath = kotlinFilePath,
            )
        }
    }
}
