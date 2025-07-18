/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.DtoEntityRenderModel

/**
 * Generate the content for the template EntityDtoTemplateRenderer filled up
 * with the content of the passed models.
 */
object EntityDtoTemplateRenderer {

    fun renderTemplate(model: DtoEntityRenderModel): String {
        return """
          |package my.example.businessproject.dto
          |
          |data class ${model.kotlinDtoClassName}( 
          |    val ${model.entityPrimaryField}Code: String,
          |    val productName: String,
          |)
          |
        """.trimMargin(marginPrefix = "|")
    }
}