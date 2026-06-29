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
          |<html lang="en">
          |<body>
          |    <ul>
          |    <li>default keepA
          |    keepB</li>
          |    <li>rbb keepAkeepB</li>
          |    <li>rba keepAkeepB</li>
          |    <li>rlb keepAkeepB</li>
          |    <li>rla keepA    keepB</li>
          |    </ul>
          |</body>
          |</html>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "whitespace/whitespace.html"
    }
}