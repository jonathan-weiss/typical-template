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
          |klb[keepA
          |   keepB]
          |kla[keepA
          |
          |keepB]
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: HtmlListModel): String {
      return "whitespace-keep.html"
    }
}