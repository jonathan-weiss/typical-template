/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.HtmlListModel

/**
 * Generate the content for the template `NestingRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `nesting.html`
 * - path: `nesting/nesting.html`
 */
object NestingRenderer {

    fun renderTemplate(model: HtmlListModel): String {
        return """
          |<ul>
          |${ model.allListEntries.joinToString("") { entry ->  """${ if(entry.isNotBlank()) { """  <li>* ${entry}</li>
                  |""" } else { """""" } }""" } }</ul>
          |<p>tail: MARK ENTRY</p>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: HtmlListModel): String {
      return "nesting/nesting.html"
    }
}