package org.codeblessing.typicaltemplate

import org.codeblessing.typicaltemplate.contentparsing.ContentParser
import org.codeblessing.typicaltemplate.filemapping.ContentMapper
import org.codeblessing.typicaltemplate.filesearch.FileSearchLocation
import org.codeblessing.typicaltemplate.filesearch.FileTraversal
import org.codeblessing.typicaltemplate.template.TemplateConfiguration
import org.codeblessing.typicaltemplate.template.TemplateWriter
import java.nio.file.Paths
import kotlin.io.path.readText

object TypicalTemplateProcessor {

    fun processTypicalTemplate() {
        val templatingConfigurations = gatherTemplatingConfigurations()
        templatingConfigurations.forEach { templatingConfiguration ->
            val foundFiles = FileTraversal.searchFiles(templatingConfiguration.fileSearchLocations)

            foundFiles.map { file ->
                val supportedCommentStyles = ContentMapper.mapContent(file)
                val templates = ContentParser.parseContent(content = file.readText(), supportedCommentStyles)

                templates.forEach { template ->
                    TemplateWriter.writeTemplate(template, templatingConfiguration.templateConfiguration)
                }
            }
        }
    }

    private fun gatherTemplatingConfigurations(): List<TemplatingConfiguration> {
        // TODO support env/system-props/args

        val angularRootDirectory = Paths.get("/Users/jweiss/private-work/senegal/typical-template/src/test/resources/org/codeblessing/typicaltemplate/contentparsing")

        val rootDirectoriesToSearch = listOf(
            FileSearchLocation(
                rootDirectoryToSearch = angularRootDirectory,
                filenameMatchingPattern = ContentMapper.HTML_FILENAME_REGEX,
            ),
        )
        val srcBaseDir = Paths.get("/Users/jweiss/private-work/senegal/typical-template/build")

        val templateConfiguration = TemplateConfiguration(
            templateBaseSrcPath = srcBaseDir,
            templateBaseTestSrcPath = srcBaseDir,
        )
        val configuration = TemplatingConfiguration(
            fileSearchLocations = rootDirectoriesToSearch,
            templateConfiguration = templateConfiguration,
        )
        return listOf(configuration)
    }
}
