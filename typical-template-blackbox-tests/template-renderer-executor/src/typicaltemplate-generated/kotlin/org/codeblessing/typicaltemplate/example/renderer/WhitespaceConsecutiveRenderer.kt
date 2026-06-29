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
          |<html lang="en">
          |<body>
          |<ul>
          |<li>merge keepAkeepB</li>
          |</ul>
          |</body>
          |</html>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: HtmlListModel): String {
      return "whitespace-consecutive.html"
    }
}