/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer

import org.codeblessing.typicaltemplate.example.renderer.model.BlackboxDefaultModel

/**
 * Generate the content for the template `PlainRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `nesting-template-renderer.html`
 * - path: `nestingtemplaterenderer/nesting-template-renderer.html`
 */
object PlainRenderer : RendererWithBlackboxDefaultModel {

    override fun renderTemplate(model: BlackboxDefaultModel): String {
        return """
          |        <li class="plain">TOKEN rendered on DAY</li>
          |
        """.trimMargin(marginPrefix = "|")
    }

    override fun filePath(model: BlackboxDefaultModel): String {
      return "nestingtemplaterenderer/nesting-template-renderer.html"
    }
}