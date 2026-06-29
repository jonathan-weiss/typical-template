/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import java.time.DayOfWeek

/**
 * Generate the content for the template `FancyRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `nesting-template-renderer.html`
 * - path: `nestingtemplaterenderer/nesting-template-renderer.html`
 */
object FancyRenderer {

    fun renderTemplate(): String {
        return """
          |<html lang="en">
          |<body>
          |    <ul>
          |        <li class="fancy">REPLACED-IN-A rendered on ${DayOfWeek.MONDAY}</li>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "nestingtemplaterenderer/fancy-output.html"
    }
}