/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.HtmlListModel

/**
 * Generate the content for the template `WhitespaceRemoveRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `whitespace-remove.html`
 * - path: `whitespace-remove.html`
 */
object WhitespaceRemoveRenderer {

    fun renderTemplate(model: HtmlListModel): String {
        return """
          |<ul>
          |<li>rbb keepAkeepB</li>
          |<li>rba keepAkeepB</li>
          |<li>rlb keepAkeepB</li>
          |<li>rla keepAkeepB</li>
          |</ul>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: HtmlListModel): String {
      return "whitespace-remove.html"
    }
}