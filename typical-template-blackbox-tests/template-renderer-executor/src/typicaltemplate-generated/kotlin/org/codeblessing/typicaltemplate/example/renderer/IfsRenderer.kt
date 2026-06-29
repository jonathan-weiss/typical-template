/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.BlackboxDefaultModel

/**
 * Generate the content for the template `IfsRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `nesting-ifs.html`
 * - path: `nestingifs/nesting-ifs.html`
 */
object IfsRenderer : RendererWithBlackboxDefaultModel {

    override fun renderTemplate(model: BlackboxDefaultModel): String {
        return """
          |<html lang="en">
          |<body>
          |    <ul>
          |${ if(model.isTrueAttribute) { """        <li class="book">A book</li>
              |${ if(model.isTrueAttribute) { """        <li class="badge">highlighted</li>
                  |""" } else { """        <li class="badge">plain</li>
                  |""" } }""" } else if(model.isTrueAttribute) { """        <li class="movie">A movie</li>
              |""" } else { """        <li class="other">Something else</li>
              |""" } }
        """.trimMargin(marginPrefix = "|")
    }

    override fun filePath(model: BlackboxDefaultModel): String {
      return "nestingifs/nesting-ifs.html"
    }
}