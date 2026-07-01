package org.codeblessing.tavnit.application

import org.codeblessing.tavnit.CommentStyle
import org.codeblessing.tavnit.RelativeFile
import org.codeblessing.tavnit.TemplatingConfiguration
import org.codeblessing.tavnit.TavnitProcessorApi
import org.codeblessing.tavnit.contentparsing.ContentParser
import org.codeblessing.tavnit.filemapping.ContentMapper
import org.codeblessing.tavnit.filesearch.FileTraversal
import org.codeblessing.tavnit.templaterenderer.TemplateRendererClassContentCreator
import org.codeblessing.tavnit.templaterenderer.TemplateRendererContentCreator
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createParentDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText

class TavnitProcessor: TavnitProcessorApi {

    override fun processTavnit(
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
            val kotlinTemplateRendererClassContent = TemplateRendererClassContentCreator.wrapInKotlinClassContent(filepath, templateRendererDescription, kotlinTemplateContent)
            val kotlinFilePath = templateRendererDescription.templateRendererClass.classFilePath(targetBasePath)

            TemplateRendererClass(
                templateRendererDescription = templateRendererDescription,
                templateRendererClassContent = kotlinTemplateRendererClassContent,
                templateRendererClassFilePath = kotlinFilePath,
            )
        }
    }
}
