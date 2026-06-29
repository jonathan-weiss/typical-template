/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.BlackboxDefaultModel

/**
 * Generate the content for the template `HtmlListPageRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `foreach.html`
 * - path: `foreach/foreach.html`
 */
object HtmlListPageRenderer : RendererWithBlackboxDefaultModel {

    override fun renderTemplate(model: BlackboxDefaultModel): String {
        return """
          |<html lang="en">
          |
          |
          |<head><title>${model.simpleName}</title></head>
          |<body>
          |<p>Here are the ${model.simpleName.lowercase()}:</p>
          |<ul>${ model.allListEntries.joinToString("") { pageArticleTitle ->  """
              |    <li>${pageArticleTitle}</li>""" } }
          |</ul>
          |
          |</body>
          |</html>
          |
        """.trimMargin(marginPrefix = "|")
    }

    override fun filePath(model: BlackboxDefaultModel): String {
      return "foreach/foreach.html"
    }
}