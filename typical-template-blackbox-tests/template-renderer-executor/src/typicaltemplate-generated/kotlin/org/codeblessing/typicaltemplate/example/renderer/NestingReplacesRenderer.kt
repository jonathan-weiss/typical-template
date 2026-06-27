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
          |within-comment-order: THREE
          |innermost-first: THREE
          |expression: ${40 + 2}
          |after-inner: ZERO
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "nestingreplaces/nesting-replaces.html"
    }
}