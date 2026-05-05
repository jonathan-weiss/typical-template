/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.SummaryRenderModel

/**
 * Generate the content for the template SummaryExtensionRenderer filled up
 * with the content of the passed models.
 */
object SummaryExtensionRenderer {

    fun renderTemplate(model: SummaryRenderModel): String {
        return """
          |package my.example.businessproject.summary
          |// Auto-generated extensions - do not modify
          |
          |fun ${model.summaryClassName}.label(): String = "Summary: ${model.summaryClassName}"
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: SummaryRenderModel): String {
      return "my/example/businessproject/summary/OrderSummary.kt"
    }
}