package org.codeblessing.typicaltemplate

import org.codeblessing.typicaltemplate.contentparsing.ContentParser
import org.codeblessing.typicaltemplate.filemapping.ContentMapper
import org.codeblessing.typicaltemplate.filesearch.FileTraversal
import org.codeblessing.typicaltemplate.templaterenderer.TemplateRendererWriter
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.pathString
import kotlin.io.path.readText
import kotlin.io.path.relativeTo

class TypicalTemplateProcessor: TypicalTemplateProcessorApi {

    override fun processTypicalTemplate(
        templatingConfigurations: List<TemplatingConfiguration>,
    ): Map<TemplatingConfiguration, List<Path>> {
        val createdTemplateFactories: Map<TemplatingConfiguration, MutableList<Path>> = templatingConfigurations
            .associateWith { mutableListOf()}
        templatingConfigurations.forEach { templatingConfiguration ->
            val foundFiles = FileTraversal.searchFiles(templatingConfiguration.fileSearchLocations)

            foundFiles.map { foundFile ->
                val file = foundFile.filePath
                val supportedCommentStyles = ContentMapper.mapContent(file)
                val templates = try {
                    ContentParser.parseContent(content = file.readText(), supportedCommentStyles)
                } catch (e: Exception) {
                    throw RuntimeException("Error parsing template content of file ${file.absolutePathString()}", e)
                }

                templates.forEach { template ->
                    val relativeFilePath = file.relativeTo(foundFile.rootDirectory).normalize().pathString
                    val templatePath = TemplateRendererWriter.writeTemplate(relativeFilePath, template, templatingConfiguration.templateRendererConfiguration)
                    requireNotNull(createdTemplateFactories[templatingConfiguration]).add(templatePath)
                }
            }
        }
        return createdTemplateFactories
    }
}
