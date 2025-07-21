package org.codeblessing.typicaltemplate.example

import org.codeblessing.typicaltemplate.FileSearchLocation
import org.codeblessing.typicaltemplate.TemplateRendererConfiguration
import org.codeblessing.typicaltemplate.TemplatingConfiguration
import org.codeblessing.typicaltemplate.TypicalTemplateApi
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.system.exitProcess

private const val PRINT_CREATED_TEMPLATE_RENDERERS = true

fun main(args: Array<String>) {
    if(args.size != 3) {
        println("Wrong arguments!")
        println("Use <path to kotlin source> <path to html source> <path to target template renderers base dir>")
        exitProcess(1)
    }

    println("Generating template renderer with typical template")
    val config = gatherTemplatingConfigurations(
        pathToKotlinSourceTemplates = Paths.get(args[0]),
        pathToHtmlSourceTemplates = Paths.get(args[1]),
        pathToTargetTemplateRenderersBaseDir = Paths.get(args[2])
    )
    val createdTemplateRenderers = TypicalTemplateApi.runTypicalTemplate(config)

    if(PRINT_CREATED_TEMPLATE_RENDERERS) {
        printCreatedTemplateRenders(createdTemplateRenderers)
    }
}

private fun printCreatedTemplateRenders(createdTemplateRenderers: Map<TemplatingConfiguration, List<Path>>) {
    for ((configuration, paths) in createdTemplateRenderers) {
        println("Configuration: $configuration created paths:")
        for (path in paths) {
            println("- TemplateRenderer $path")
        }
    }
}

private fun gatherTemplatingConfigurations(
    pathToKotlinSourceTemplates: Path,
    pathToHtmlSourceTemplates: Path,
    pathToTargetTemplateRenderersBaseDir: Path,
): List<TemplatingConfiguration> {
    val rootDirectoriesToSearch = listOf(
        FileSearchLocation(
            rootDirectoryToSearch = pathToKotlinSourceTemplates,
            filenameMatchingPattern = Regex(".*\\.kt"),
        ),
        FileSearchLocation(
            rootDirectoryToSearch = pathToHtmlSourceTemplates,
            filenameMatchingPattern = Regex(".*\\.html"),
        ),
    )

    val templateRendererConfiguration = TemplateRendererConfiguration(
        templateRendererTargetSourceBasePath = pathToTargetTemplateRenderersBaseDir,
    )
    val configuration = TemplatingConfiguration(
        fileSearchLocations = rootDirectoriesToSearch,
        templateRendererConfiguration = templateRendererConfiguration,
    )
    return listOf(configuration)
}
