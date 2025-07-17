package org.codeblessing.typicaltemplate

import org.junit.jupiter.api.Test
import java.nio.file.Paths

class TypicalTemplatePlaygroundTest {
    @Test
    fun `run typical template`() {
        val resultPaths = TypicalTemplateApi.runTypicalTemplate(gatherTemplatingConfigurations())
        for ((configuration, paths) in resultPaths) {
            println("Configuration: $configuration created paths:")
            for (path in paths) {
                println("- TemplateFactory $path")
            }
        }
    }

    private fun gatherTemplatingConfigurations(): List<TemplatingConfiguration> {
        // TODO support env/system-props/args

        val angularRootDirectory = Paths.get("/Users/jweiss/private-work/senegal/typical-template/src/test/resources/org/codeblessing/typicaltemplate/contentparsing")

        val rootDirectoriesToSearch = listOf(
            FileSearchLocation(
                rootDirectoryToSearch = angularRootDirectory,
                filenameMatchingPattern = Regex(".*\\.(html|xhtml)"),
            ),
        )
        val srcBaseDir = Paths.get("/Users/jweiss/private-work/senegal/typical-template/build")

        val templateRendererConfiguration = TemplateRendererConfiguration(
            templateRendererTargetSourceBasePath = srcBaseDir,
        )
        val configuration = TemplatingConfiguration(
            fileSearchLocations = rootDirectoriesToSearch,
            templateRendererConfiguration = templateRendererConfiguration,
        )
        return listOf(configuration)
    }
}
