/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.SummaryRenderModel

/**
 * Generate the content for the template `SummaryClassRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `OrderSummary.kt`
 * - path: `my/example/businessproject/summary/OrderSummary.kt`
 */
object SummaryClassRenderer {

    fun renderTemplate(model: SummaryRenderModel): String {
        return """
          |package my.example.businessproject.summary
          |
          |
          |
          |// Auto-generated summary class - do not modify
          |
          |@Suppress("unused")
          |data class ${model.summaryClassName}(
          |${ model.fields.joinToString("") { field ->  """
              |${ field.validationRules.joinToString("") { rule ->  """
                  |    // ${rule}
                  |""" } }
              |
              |${ if(field.isNullable) { """
                  |    val ${field.fieldName}: ${field.fieldType}?,
                  |""" } else if(field.isList) { """
                  |    val ${field.fieldName}: List<${field.fieldType}>,
                  |""" } else { """
                  |    val ${field.fieldName}: ${field.fieldType},
                  |""" } }
              |""" } }
          |)
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: SummaryRenderModel): String {
      return "my/example/businessproject/summary/${model.summaryClassName}.kt"
    }
}