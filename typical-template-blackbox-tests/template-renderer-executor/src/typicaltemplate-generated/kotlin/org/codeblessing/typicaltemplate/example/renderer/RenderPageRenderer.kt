/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.RenderItemRenderer

/**
 * Generate the content for the template `RenderPageRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `render-template.html`
 * - path: `rendertemplate/render-template.html`
 */
object RenderPageRenderer {

    fun renderTemplate(): String {
        return """
          |    default (indent before the placeholder is removed; embedded lines are not re-indented):
          |    <ul>
          |${RenderItemRenderer.renderTemplate()}    </ul>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "rendertemplate/render-template.html"
    }
}