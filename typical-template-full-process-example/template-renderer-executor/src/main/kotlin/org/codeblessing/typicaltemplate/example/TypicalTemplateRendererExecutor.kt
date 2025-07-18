package org.codeblessing.typicaltemplate.example

import org.codeblessing.typicaltemplate.example.renderer.EntityDtoTemplateRenderer
import org.codeblessing.typicaltemplate.example.renderer.model.DtoEntityRenderModel
import org.codeblessing.typicaltemplate.example.renderer.model.DtoFieldRenderModel
import java.nio.file.Paths
import java.util.Locale
import java.util.Locale.getDefault
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.system.exitProcess

private const val PRINT_KOTLIN_FILE = true

fun main(args: Array<String>) {
    if(args.size != 1) {
        println("Wrong arguments!")
        println("Use <path to generated target files>")
        exitProcess(1)
    }

    val pathToGeneratedTargetFiles = Paths.get(args.single())

    println("Use template renderer with typical template")

    val dtoRenderModels = listOf(
        createDtoEntity("Category"),
        createDtoEntity("Cart"),
        createDtoEntity("User"),
        createDtoEntity("RelatedProduct"),
    )

    dtoRenderModels.forEach { dtoRenderModel ->
        val kotlinContent = EntityDtoTemplateRenderer.renderTemplate(model = dtoRenderModel)
        if(PRINT_KOTLIN_FILE) {
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(" ${dtoRenderModel.entityName} (${dtoRenderModel.dtoPackageName}.${dtoRenderModel.kotlinDtoClassName}) ")
            println(" ------------------------------------------------------------------------------------------------------------ ")
            println(kotlinContent)
        }

        val kotlinFilePath = pathToGeneratedTargetFiles
            .resolve(dtoRenderModel.dtoNestedPackageDirectory)
            .resolve(dtoRenderModel.kotlinDtoFileName)

        kotlinFilePath.parent.createDirectories()
        kotlinFilePath.writeText(kotlinContent)
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
            createDtoField("${entityNameDecapitalized}Description"),
        )
    )
}

private fun createDtoField(fieldName: String, type: String = "String", isNullable: Boolean = true): DtoFieldRenderModel {
    return DtoFieldRenderModel(
        fieldName = fieldName,
        fieldTypeName = type,
        isNullable = isNullable
    )
}
