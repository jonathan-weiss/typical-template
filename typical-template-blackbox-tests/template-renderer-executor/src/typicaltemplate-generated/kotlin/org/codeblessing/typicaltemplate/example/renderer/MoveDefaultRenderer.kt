/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer



/**
 * Generate the content for the template `MoveDefaultRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `move-default.html`
 * - path: `move/move-default.html`
 */
object MoveDefaultRenderer {

    fun renderTemplate(): String {
        return """
          |<ul>
          |    <li id="b1">Clean Code</li>
          |</ul>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "move/move-default.html"
    }
}