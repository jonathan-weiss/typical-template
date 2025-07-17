package org.codeblessing.typicaltemplate

import org.codeblessing.typicaltemplate.contentparsing.ContentParser
import org.codeblessing.typicaltemplate.filemapping.ContentMapper
import org.codeblessing.typicaltemplate.filesearch.FileTraversal
import org.codeblessing.typicaltemplate.templaterenderer.TemplateRendererWriter
import java.nio.file.Path
import kotlin.io.path.readText

class TypicalTemplateProcessor: TypicalTemplateProcessorApi {

    override fun processTypicalTemplate(
        templatingConfigurations: List<TemplatingConfiguration>,
    ): Map<TemplatingConfiguration, List<Path>> {
        val createdTemplateFactories: Map<TemplatingConfiguration, MutableList<Path>> = templatingConfigurations
            .associateWith { mutableListOf()}
        templatingConfigurations.forEach { templatingConfiguration ->
            val foundFiles = FileTraversal.searchFiles(templatingConfiguration.fileSearchLocations)

            foundFiles.map { file ->
                val supportedCommentStyles = ContentMapper.mapContent(file)
                val templates = ContentParser.parseContent(content = file.readText(), supportedCommentStyles)

                templates.forEach { template ->
                    val templatePath = TemplateRendererWriter.writeTemplate(template, templatingConfiguration.templateRendererConfiguration)
                    requireNotNull(createdTemplateFactories[templatingConfiguration]).add(templatePath)
                }
            }
        }
        return createdTemplateFactories
    }
}
