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
          |/**
          | * The ${model.entityName} DTO (Data Transfer Object) class.
          | */
          |data class ${model.kotlinDtoClassName}(
          |${ model.fields.joinToString("") { field ->  """
              |    val ${field.fieldName}: ${field.fieldTypeNameWithNullability},
          """ } }
          |)
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: DtoEntityRenderModel): String {
      return "my/example/businessproject/dto/ProductDto.kt"
    }
}