/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer



/**
 * Generate the content for the template `NestingReplacesRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `nesting-replaces.html`
 * - path: `nestingreplaces/nesting-replaces.html`
 */
object NestingReplacesRenderer {

    fun renderTemplate(): String {
        return """
          |<ul>
          |<li>within-comment-order: THREE</li>
          |<li>innermost-first: THREE</li>
          |<li>expression: ${40 + 2}</li>
          |<li>after-inner: ZERO</li>
          |</ul>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "nestingreplaces/nesting-replaces.html"
    }
}