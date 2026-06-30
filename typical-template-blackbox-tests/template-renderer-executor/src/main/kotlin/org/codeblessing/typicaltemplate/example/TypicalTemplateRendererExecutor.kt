package org.codeblessing.typicaltemplate.example

import org.codeblessing.typicaltemplate.example.SourceType.HTML
import org.codeblessing.typicaltemplate.example.SourceType.KOTLIN
import org.codeblessing.typicaltemplate.example.renderer.EntityDtoTemplateRenderer
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
import org.codeblessing.typicaltemplate.example.renderer.EnumRenderer
import org.codeblessing.typicaltemplate.example.renderer.WhitespaceHtmlRenderer
import org.codeblessing.typicaltemplate.example.renderer.model.BlackboxDefaultModel
import org.codeblessing.typicaltemplate.example.renderer.model.DtoEntityRenderModel
import org.codeblessing.typicaltemplate.example.renderer.model.DtoFieldRenderModel
import org.codeblessing.typicaltemplate.example.renderer.model.EnumRenderModel
import java.nio.file.Path
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

    fun SourceType.basePathToGeneratedFiles(): Path {
        return when(this) {
            KOTLIN -> pathToGeneratedKotlinFiles
            HTML -> pathToGeneratedHtmlFiles
        }

    }

    ALL_DEFAULT_RENDERERS.forEach { defaultRenderer ->
        val blackboxDefaultModel = BlackboxDefaultModel()
        val content = defaultRenderer.renderer.renderTemplate(blackboxDefaultModel)
        val filepath = defaultRenderer.renderer.filePath(blackboxDefaultModel)

        writeGeneratedContentToFile(
            sourceType = defaultRenderer.sourceType,
            model = blackboxDefaultModel,
            content = content,
            filepath = defaultRenderer.sourceType.basePathToGeneratedFiles().resolve(filepath)
        )
    }

    enumTypes().forEach { enumType ->
        val blackboxDefaultModel = BlackboxDefaultModel()
        val content = EnumRenderer.renderTemplate(enumType)
        val filepath = EnumRenderer.filePath(enumType)

        val sourceType = KOTLIN
        writeGeneratedContentToFile(
            sourceType = sourceType,
            model = blackboxDefaultModel,
            content = content,
            filepath = sourceType.basePathToGeneratedFiles().resolve(filepath)
        )
    }

    val dtoRenderModels = listOf(
        createDtoEntity("Category"),
        createDtoEntity("Cart"),
        createDtoEntity("User"),
        createDtoEntity("RelatedProduct"),
    )

    dtoRenderModels.forEach { dtoRenderModel ->
        val sourceType = KOTLIN
        val content = EntityDtoTemplateRenderer.renderTemplate(model = dtoRenderModel)
        val filepath = sourceType.basePathToGeneratedFiles()
            .resolve(dtoRenderModel.dtoNestedPackageDirectory)
            .resolve(dtoRenderModel.kotlinDtoFileName)

        writeGeneratedContentToFile(
            sourceType = sourceType,
            model = dtoRenderModel,
            content = content,
            filepath = sourceType.basePathToGeneratedFiles().resolve(filepath)
        )
    }
}

private fun enumTypes(): List<EnumRenderModel> {
    return listOf(
        EnumRenderModel("WorkStatus", listOf("new", "inProgress", "finished")),
        EnumRenderModel("Season", listOf("winter", "spring", "summer", "fall")),
        EnumRenderModel("PaymentStatus", listOf("PENDING", "PAID", "REFUNDED")),
    )
}

private fun <M> writeGeneratedContentToFile(
    sourceType: SourceType,
    model: M,
    content: String,
    filepath: Path
) {
    filepath.parent.createDirectories()
    filepath.writeText(content)

    if(PRINT_GENERATED_CONTENT) {
        println(" ------------------------------------------------------------------------------------------------------------ ")
        println(" $sourceType (Model: ${model}) ")
        println(" $filepath ")
        println(" ------------------------------------------------------------------------------------------------------------ ")
        println(content)
    }
}

private fun createDtoEntity(entityName: String): DtoEntityRenderModel {
    val entityNameDecapitalized = entityName.replaceFirstChar { it.lowercase() }
    return DtoEntityRenderModel(
        entityName = entityName,
        fields = listOf(
            createDtoField("${entityNameDecapitalized}Key"),
            createDtoField("${entityNameDecapitalized}Ean", type = "Int"),
            createDtoField("${entityNameDecapitalized}Text"),
            createDtoField("${entityNameDecapitalized}Description", isNullable = true),
        )
    )
}

private fun createDtoField(fieldName: String, type: String = "String", isNullable: Boolean = false): DtoFieldRenderModel {
    return DtoFieldRenderModel(
        fieldName = fieldName,
        fieldTypeName = type,
        isNullable = isNullable
    )
}
