/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.BlackboxDefaultModel
import java.time.DayOfWeek

/**
 * Generate the content for the template `FancyRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `nesting-template-renderer.html`
 * - path: `nestingtemplaterenderer/nesting-template-renderer.html`
 */
object FancyRenderer : RendererWithBlackboxDefaultModel {

    override fun renderTemplate(model: BlackboxDefaultModel): String {
        return """
          |<html lang="en">
          |<body>
          |    <ul>
          |        <li class="fancy">REPLACED-IN-A rendered on ${DayOfWeek.MONDAY}</li>
          |
        """.trimMargin(marginPrefix = "|")
    }

    override fun filePath(model: BlackboxDefaultModel): String {
      return "nestingtemplaterenderer/fancy-output.html"
    }
}