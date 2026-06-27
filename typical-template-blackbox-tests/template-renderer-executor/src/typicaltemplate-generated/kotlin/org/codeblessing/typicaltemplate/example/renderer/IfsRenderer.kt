/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.IfModel

/**
 * Generate the content for the template `IfsRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `nesting-ifs.html`
 * - path: `nestingifs/nesting-ifs.html`
 */
object IfsRenderer {

    fun renderTemplate(model: IfModel): String {
        return """
          |${ if(model.isBook) { """BOOK
              |${ if(model.highlighted) { """- highlighted
                  |""" } else { """- plain
                  |""" } }""" } else if(model.isMovie) { """MOVIE
              |""" } else { """OTHER
              |""" } }
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: IfModel): String {
      return "nestingifs/nesting-ifs.html"
    }
}