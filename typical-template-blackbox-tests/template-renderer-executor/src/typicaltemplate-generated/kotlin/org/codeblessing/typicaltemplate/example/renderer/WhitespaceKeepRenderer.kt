/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.HtmlListModel

/**
 * Generate the content for the template `WhitespaceKeepRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `whitespace-keep.html`
 * - path: `whitespace-keep.html`
 */
object WhitespaceKeepRenderer {

    fun renderTemplate(model: HtmlListModel): String {
        return """
          |<html lang="en">
          |<body>
          |<ul>
          |<li>klb keepA
          |   keepB</li>
          |<li>kla keepA
          |
          |keepB</li>
          |</ul>
          |</body>
          |</html>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: HtmlListModel): String {
      return "whitespace-keep.html"
    }
}