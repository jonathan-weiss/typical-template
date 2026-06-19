/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.HtmlListModel

/**
 * Generate the content for the template `HtmlListPageRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `news.html`
 * - path: `news.html`
 */
object HtmlListPageRenderer {

    fun renderTemplate(listPageModel: HtmlListModel): String {
        return """
          |<html lang="en">
          |
          |
          |<head><title>${listPageModel.pageTitle}</title></head>
          |<body>
          |<p>Here are the ${listPageModel.pageTitle.lowercase()}:</p>
          |<ul>${ listPageModel.allListEntries.joinToString("") { pageArticleTitle ->  """
              |    <li>${pageArticleTitle}</li>""" } }
          |</ul>
          |
          |</body>
          |</html>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(listPageModel: HtmlListModel): String {
      return "news.html"
    }
}