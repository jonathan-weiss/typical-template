/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer



/**
 * Generate the content for the template `WhitespaceHtmlRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `whitespace.html`
 * - path: `whitespace/whitespace.html`
 */
object WhitespaceHtmlRenderer {

    fun renderTemplate(): String {
        return """
          |default[keepA
          |keepB]
          |rbb[keepAkeepB]
          |rba[keepAkeepB]
          |rlb[keepAkeepB]
          |rla[keepAkeepB]
          |klb[keepA
          |   keepB]
          |kla[keepA
          |
          |keepB]
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "whitespace/whitespace.html"
    }
}