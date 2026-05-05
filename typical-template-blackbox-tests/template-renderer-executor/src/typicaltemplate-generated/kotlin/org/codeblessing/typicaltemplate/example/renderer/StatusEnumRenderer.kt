/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.StatusEnumRenderModel

/**
 * Generate the content for the template StatusEnumRenderer filled up
 * with the content of the passed models.
 */
object StatusEnumRenderer {

    fun renderTemplate(model: StatusEnumRenderModel): String {
        return """
          |package my.example.businessproject.domain
          |
          |
          |
          |enum class ${model.enumName} {
          |${ model.statusValues.joinToString("") { statusValue ->  """
              |    ${statusValue},
              |
          """ } }
          |}
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: StatusEnumRenderModel): String {
      return "my/example/businessproject/domain/${model.enumName}Enum.kt"
    }
}