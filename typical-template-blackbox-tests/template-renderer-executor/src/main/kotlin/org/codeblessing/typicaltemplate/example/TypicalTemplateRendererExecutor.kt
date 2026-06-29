package org.codeblessing.typicaltemplate.example

import org.codeblessing.typicaltemplate.example.SourceType.HTML
import org.codeblessing.typicaltemplate.example.renderer.FancyRenderer
import org.codeblessing.typicaltemplate.example.renderer.ForeachRenderer
import org.codeblessing.typicaltemplate.example.renderer.IfsRenderer
import org.codeblessing.typicaltemplate.example.renderer.MoveDefaultRenderer
import org.codeblessing.typicaltemplate.example.renderer.MoveRenderer
import org.codeblessing.typicaltemplate.example.renderer.NestingRenderer
import org.codeblessing.typicaltemplate.example.renderer.NestingReplacesRenderer
import org.codeblessing.typicaltemplate.example.renderer.PlainRenderer
import org.codeblessing.typicaltemplate.example.renderer.RenderItemRenderer
import org.codeblessing.typicaltemplate.example.renderer.RenderPageRenderer
import org.codeblessing.typicaltemplate.example.renderer.RendererWithBlackboxDefaultModel
import org.codeblessing.typicaltemplate.example.renderer.WhitespaceHtmlRenderer
import org.codeblessing.typicaltemplate.example.renderer.model.BlackboxDefaultModel
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.system.exitProcess

private enum class SourceType {
    KOTLIN,
    HTML,
    ;
}

private data class RendererDeclaration(
    val sourceType: SourceType,
    val renderer: RendererWithBlackboxDefaultModel,
)

private const val PRINT_GENERATED_CONTENT = true
private val ALL_DEFAULT_RENDERERS = listOf(
    RendererDeclaration(sourceType = HTML, renderer = FancyRenderer),
    RendererDeclaration(sourceType = HTML, renderer = ForeachRenderer),
    RendererDeclaration(sourceType = HTML, renderer = IfsRenderer),
    RendererDeclaration(sourceType = HTML, renderer = MoveDefaultRenderer),
    RendererDeclaration(sourceType = HTML, renderer = MoveRenderer),
    RendererDeclaration(sourceType = HTML, renderer = NestingRenderer),
    RendererDeclaration(sourceType = HTML, renderer = NestingReplacesRenderer),
    RendererDeclaration(sourceType = HTML, renderer = PlainRenderer),
    RendererDeclaration(sourceType = HTML, renderer = RenderItemRenderer),
    RendererDeclaration(sourceType = HTML, renderer = RenderPageRenderer),
    RendererDeclaration(sourceType = HTML, renderer = WhitespaceHtmlRenderer),
)

fun main(args: Array<String>) {
    if(args.size != 2) {
        println("Wrong arguments!")
        println("Use <path to generated kotlin files> <path to generated HTML files>")
        exitProcess(1)
    }

    val pathToGeneratedKotlinFiles = Paths.get(args[0])
    val pathToGeneratedHtmlFiles = Paths.get(args[1])

    println("Use template renderer with typical template")


    ALL_DEFAULT_RENDERERS.forEach { defaultRenderer ->
        val blackboxDefaultModel = BlackboxDefaultModel()
        val content = defaultRenderer.renderer.renderTemplate(blackboxDefaultModel)
        val filepath = defaultRenderer.renderer.filePath(blackboxDefaultModel)

        val basePathToGeneratedFiles = when(defaultRenderer.sourceType) {
            SourceType.KOTLIN -> pathToGeneratedKotlinFiles
            HTML -> pathToGeneratedHtmlFiles
        }
        val targetFilePath = basePathToGeneratedFiles
            .resolve(filepath)

        targetFilePath.parent.createDirectories()
        targetFilePath.writeText(content)

        if(PRINT_GENERATED_CONTENT) {
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(" ${defaultRenderer.sourceType} (${defaultRenderer.renderer::class.simpleName}) ")
            println(" $targetFilePath ")
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(content)
        }
    }
}
