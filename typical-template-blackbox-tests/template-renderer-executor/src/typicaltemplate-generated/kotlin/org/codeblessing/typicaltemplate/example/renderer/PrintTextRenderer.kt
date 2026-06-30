/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.BlackboxDefaultModel

/**
 * Generate the content for the template `PrintTextRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `print-text.html`
 * - path: `printtext/print-text.html`
 */
object PrintTextRenderer : RendererWithBlackboxDefaultModel {

    override fun renderTemplate(model: BlackboxDefaultModel): String {
        return """
          |<html lang="en">
          |<body>
          |    <ul>
          |        <li>normal text STARTinserted-textEND</li>
          |        <li>blanks only START   END</li>
          |    </ul>
          |</body>
          |</html>
          |
        """.trimMargin(marginPrefix = "|")
    }

    override fun filePath(model: BlackboxDefaultModel): String {
      return "printtext/print-text.html"
    }
}