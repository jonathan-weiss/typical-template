package org.codeblessing.typicaltemplate.example

import org.codeblessing.typicaltemplate.example.renderer.EntityDtoTemplateRenderer
import org.codeblessing.typicaltemplate.example.renderer.HtmlListPageRenderer
import org.codeblessing.typicaltemplate.example.renderer.StatusEnumRenderer
import org.codeblessing.typicaltemplate.example.renderer.SummaryClassRenderer
import org.codeblessing.typicaltemplate.example.renderer.SummaryExtensionRenderer
import org.codeblessing.typicaltemplate.example.renderer.WhitespaceConsecutiveRenderer
import org.codeblessing.typicaltemplate.example.renderer.WhitespaceKeepRenderer
import org.codeblessing.typicaltemplate.example.renderer.WhitespaceRemoveRenderer
import org.codeblessing.typicaltemplate.example.renderer.model.DtoEntityRenderModel
import org.codeblessing.typicaltemplate.example.renderer.model.DtoFieldRenderModel
import org.codeblessing.typicaltemplate.example.renderer.model.HtmlListModel
import org.codeblessing.typicaltemplate.example.renderer.model.StatusEnumRenderModel
import org.codeblessing.typicaltemplate.example.renderer.model.SummaryFieldRenderModel
import org.codeblessing.typicaltemplate.example.renderer.model.SummaryRenderModel
import java.nio.file.Paths
import java.util.Locale.getDefault
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.system.exitProcess

private const val PRINT_GENERATED_CONTENT = true

fun main(args: Array<String>) {
    if(args.size != 2) {
        println("Wrong arguments!")
        println("Use <path to generated kotlin files> <path to generated HTML files>")
        exitProcess(1)
    }

    val pathToGeneratedKotlinFiles = Paths.get(args[0])
    val pathToGeneratedHtmlFiles = Paths.get(args[1])

    println("Use template renderer with typical template")

    // GENERATE KOTLIN FILES
    val dtoRenderModels = listOf(
        createDtoEntity("Category"),
        createDtoEntity("Cart"),
        createDtoEntity("User"),
        createDtoEntity("RelatedProduct"),
    )

    dtoRenderModels.forEach { dtoRenderModel ->
        val kotlinContent = EntityDtoTemplateRenderer.renderTemplate(model = dtoRenderModel)
        if(PRINT_GENERATED_CONTENT) {
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(" ${dtoRenderModel.entityName} (${dtoRenderModel.dtoPackageName}.${dtoRenderModel.kotlinDtoClassName}) ")
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(kotlinContent)
        }

        val kotlinFilePath = pathToGeneratedKotlinFiles
            .resolve(dtoRenderModel.dtoNestedPackageDirectory)
            .resolve(dtoRenderModel.kotlinDtoFileName)

        kotlinFilePath.parent.createDirectories()
        kotlinFilePath.writeText(kotlinContent)
    }

    // GENERATE KOTLIN ENUM FILES
    val statusEnumRenderModels = listOf(
        StatusEnumRenderModel(
            enumName = "PaymentStatus",
            statusValues = listOf("PENDING", "PAID", "REFUNDED"),
        ),
    )

    statusEnumRenderModels.forEach { enumRenderModel ->
        val kotlinContent = StatusEnumRenderer.renderTemplate(model = enumRenderModel)
        if(PRINT_GENERATED_CONTENT) {
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(" ${enumRenderModel.enumName} enum ")
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(kotlinContent)
        }
        val kotlinFilePath = pathToGeneratedKotlinFiles.resolve(StatusEnumRenderer.filePath(enumRenderModel))
        kotlinFilePath.parent.createDirectories()
        kotlinFilePath.writeText(kotlinContent)
    }

    // GENERATE KOTLIN SUMMARY FILES
    val summaryRenderModels = listOf(
        SummaryRenderModel(
            summaryClassName = "PaymentSummary",
            fields = listOf(
                SummaryFieldRenderModel(
                    fieldName = "paymentId",
                    fieldType = "String",
                    validationRules = listOf("must not be blank", "max 64 chars"),
                ),
                SummaryFieldRenderModel(fieldName = "amount", fieldType = "Long"),
                SummaryFieldRenderModel(fieldName = "notes", fieldType = "String", isNullable = true),
                SummaryFieldRenderModel(fieldName = "tags", fieldType = "String", isList = true),
            ),
        ),
    )

    summaryRenderModels.forEach { summaryRenderModel ->
        val classContent = SummaryClassRenderer.renderTemplate(model = summaryRenderModel)
        if(PRINT_GENERATED_CONTENT) {
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(" ${summaryRenderModel.summaryClassName} summary class ")
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(classContent)
        }
        val classFilePath = pathToGeneratedKotlinFiles.resolve(SummaryClassRenderer.filePath(summaryRenderModel))
        classFilePath.parent.createDirectories()
        classFilePath.writeText(classContent)

        val extensionContent = SummaryExtensionRenderer.renderTemplate(model = summaryRenderModel)
        if(PRINT_GENERATED_CONTENT) {
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(" ${summaryRenderModel.summaryClassName} extensions ")
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(extensionContent)
        }
        val extensionFilePath = pathToGeneratedKotlinFiles
            .resolve(summaryRenderModel.summaryPackageDirectory)
            .resolve(summaryRenderModel.summaryExtensionsFileName)
        extensionFilePath.parent.createDirectories()
        extensionFilePath.writeText(extensionContent)
    }

    // GENERATE HTML FILES
    val htmlListPageModels = listOf(
        HtmlListModel(
            filenameWithoutPrefix = "gardening",
            pageTitle = "Garden ideas",
            allListEntries = listOf(
                "Drought-Proof Your Garden: Top Tips for Thriving in a Dry Summer",
                "Meet the Pollinators: Why Bees, Butterflies, and Birds Need Your Garden",
                "From Seed to Supper: How to Grow Your Own Organic Vegetables",
                "What’s Blooming Now? July’s Star Flowers and How to Care for Them",
                "The Hidden Life of Soil: Why Healthy Dirt Means a Healthy Garden",
            )
        ),
    )

    htmlListPageModels.forEach { htmlListPageModel ->
        val htmlContent = HtmlListPageRenderer.renderTemplate(listPageModel = htmlListPageModel)
        if(PRINT_GENERATED_CONTENT) {
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(" HTML PAGE FILE ${htmlListPageModel.filenameWithoutPrefix}.html (${htmlListPageModel.pageTitle}) ")
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(htmlContent)
        }

        val htmlFilePath = pathToGeneratedHtmlFiles
            .resolve("${htmlListPageModel.filenameWithoutPrefix}.html")

        htmlFilePath.parent.createDirectories()
        htmlFilePath.writeText(htmlContent)
    }

    // GENERATE WHITESPACE-COMMAND DEMO FILES
    // These templates only exercise the whitespace-handling commands around typical-template
    // comments; the model is required by the renderer signature but is not used in the templates.
    val whitespaceDemoModel = HtmlListModel(
        filenameWithoutPrefix = "unused",
        pageTitle = "unused",
        allListEntries = emptyList(),
    )
    val whitespaceDemoContentByFileName = mapOf(
        "whitespace-remove.html" to WhitespaceRemoveRenderer.renderTemplate(model = whitespaceDemoModel),
        "whitespace-keep.html" to WhitespaceKeepRenderer.renderTemplate(model = whitespaceDemoModel),
        "whitespace-consecutive.html" to WhitespaceConsecutiveRenderer.renderTemplate(model = whitespaceDemoModel),
    )
    whitespaceDemoContentByFileName.forEach { (fileName, content) ->
        if(PRINT_GENERATED_CONTENT) {
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(" WHITESPACE DEMO FILE $fileName ")
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(content)
        }
        val whitespaceFilePath = pathToGeneratedHtmlFiles.resolve(fileName)
        whitespaceFilePath.parent.createDirectories()
        whitespaceFilePath.writeText(content)
    }

}

private fun createDtoEntity(entityName: String): DtoEntityRenderModel {
    val entityNameDecapitalized = entityName.replaceFirstChar { it.lowercase(getDefault()) }
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
