package org.codeblessing.typicaltemplate.example

import examples.EntityDtoTemplateRenderer
import examples.model.DtoEntityRenderModel
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.system.exitProcess

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
        println(" ------------------ ")
        println(kotlinContent)
        println(" ------------------ ")

        val kotlinFilePath = pathToGeneratedTargetFiles
            .resolve(dtoRenderModel.dtoNestedPackageDirectory)
            .resolve(dtoRenderModel.kotlinDtoFileName)

        kotlinFilePath.parent.createDirectories()
        kotlinFilePath.writeText(kotlinContent)
    }
}

private fun createDtoEntity(entityName: String): DtoEntityRenderModel {
    return DtoEntityRenderModel(
        entityName = entityName
    )
}

