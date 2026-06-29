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
          |        <li>default is applied as only whitespaces before and after the comment START
          |        END</li>
          |        <li>default is not applied as there is text before the comment START
          |x            
          |            END</li>
          |        <li>default is not applied as there is text after the comment START
          |            x
          |            END</li>
          |        <li>rbb removes all whitespaces before comment STARTEND</li>
          |        <li>rba removes all whitespaces after comment STARTEND</li>
          |        <li>rlb removes all whitespaces and the linebreak before comment STARTEND</li>
          |        <li>rla removes all whitespaces and the linebreak after comment STARTEND</li>
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