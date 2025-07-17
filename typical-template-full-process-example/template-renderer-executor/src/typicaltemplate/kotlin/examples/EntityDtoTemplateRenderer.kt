/*
 * This file is generated using typical-template.
 */
package examples

import examples.model.DtoEntityRenderModel

/**
 * Generate the content for the template EntityDtoTemplateRenderer filled up
 * with the content of the model [examples.model.DtoEntityRenderModel].
 */
object EntityDtoTemplateRenderer {

    fun renderTemplate(model: DtoEntityRenderModel): String {
        return """
          |
          |
          |package org.codeblessing.typicaltemplate.example
          |
          |data class ProductDto(
          |    val productCode: String,
          |    val productName: String,
          |)
          |
        """.trimMargin(marginPrefix = "|")
    }
}