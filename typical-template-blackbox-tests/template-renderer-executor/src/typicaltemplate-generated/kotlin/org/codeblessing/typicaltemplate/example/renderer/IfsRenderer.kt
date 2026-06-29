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
          |${ if(model.isBook) { """<li class="book">A book</li>
              |${ if(model.highlighted) { """<li class="badge">highlighted</li>
                  |""" } else { """<li class="badge">plain</li>
                  |""" } }""" } else if(model.isMovie) { """<li class="movie">A movie</li>
              |""" } else { """<li class="other">Something else</li>
              |""" } }
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(model: IfModel): String {
      return "nestingifs/nesting-ifs.html"
    }
}