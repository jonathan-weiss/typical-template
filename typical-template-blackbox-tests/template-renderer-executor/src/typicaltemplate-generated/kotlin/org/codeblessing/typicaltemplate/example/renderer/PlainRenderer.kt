/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer



/**
 * Generate the content for the template `PlainRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `nesting-template-renderer.html`
 * - path: `nestingtemplaterenderer/nesting-template-renderer.html`
 */
object PlainRenderer {

    fun renderTemplate(): String {
        return """
          |B: TOKEN DAY
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "nestingtemplaterenderer/nesting-template-renderer.html"
    }
}