/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer



/**
 * Generate the content for the template `RenderItemRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `render-template.html`
 * - path: `rendertemplate/render-template.html`
 */
object RenderItemRenderer {

    fun renderTemplate(): String {
        return """
          |<li>line-1</li>
          |<li>line-2</li>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "rendertemplate/render-template.html"
    }
}