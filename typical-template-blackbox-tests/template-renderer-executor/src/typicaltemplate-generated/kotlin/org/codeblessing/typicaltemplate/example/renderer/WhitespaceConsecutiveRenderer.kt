/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.HtmlListModel

/**
 * Generate the content for the template `WhitespaceConsecutiveRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `whitespace-consecutive.html`
 * - path: `whitespace-consecutive.html`
 */
object WhitespaceConsecutiveRenderer {

    fun renderTemplate(model: HtmlListModel): String {
        return """
          |merge[keepAkeepB]
          |spread[start
          |
          |
          |end]
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: HtmlListModel): String {
      return "whitespace-consecutive.html"
    }
}