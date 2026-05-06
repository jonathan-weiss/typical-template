package org.codeblessing.typicaltemplate.application

import org.codeblessing.typicaltemplate.CommentStyle
import org.codeblessing.typicaltemplate.RelativeFile
import org.codeblessing.typicaltemplate.TemplatingConfiguration
import org.codeblessing.typicaltemplate.TypicalTemplateProcessorApi
import org.codeblessing.typicaltemplate.contentparsing.ContentParser
import org.codeblessing.typicaltemplate.filemapping.ContentMapper
import org.codeblessing.typicaltemplate.filesearch.FileTraversal
import org.codeblessing.typicaltemplate.templaterenderer.TemplateRendererClassContentCreator
import org.codeblessing.typicaltemplate.templaterenderer.TemplateRendererContentCreator
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText

class TypicalTemplateProcessor: TypicalTemplateProcessorApi {

    override fun processTypicalTemplate(
        templatingConfigurations: List<TemplatingConfiguration>,
    ): Map<TemplatingConfiguration, List<Path>> {
        return templatingConfigurations.associateWith { templatingConfiguration ->
            val foundFiles = FileTraversal.searchFiles(templatingConfiguration.fileSearchLocations)
            val targetBasePath = templatingConfiguration.templateRendererConfiguration.templateRendererTargetSourceBasePath

            foundFiles.flatMap { foundFile ->
                val templateRendererClasses = try {
                    parseContentAndCreateTemplateRenderers(
                        filepath = foundFile,
                        targetBasePath = targetBasePath,
                        contentToParse = foundFile.filePath.readText(),
                        supportedCommentStyles = ContentMapper.mapContent(foundFile.filePath)
                    )
                } catch (e: Exception) {
                    throw RuntimeException("Error parsing template content of file ${foundFile.filePath.absolutePathString()}", e)
                }
                templateRendererClasses
                    .onEach { templateRendererClass ->
                        val templateRendererClassFilePath = templateRendererClass.templateRendererClassFilePath
                        templateRendererClassFilePath.createParentDirectories()
                        templateRendererClassFilePath.writeText(templateRendererClass.templateRendererClassContent)
                    }
                    .map { templateRendererClass -> templateRendererClass.templateRendererClassFilePath }
            }
        }
    }

    private fun parseContentAndCreateTemplateRenderers(
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
