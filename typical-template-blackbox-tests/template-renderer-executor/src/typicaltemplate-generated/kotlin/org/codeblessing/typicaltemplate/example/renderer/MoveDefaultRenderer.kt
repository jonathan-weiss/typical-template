/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer



/**
 * Generate the content for the template `MoveDefaultRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `move-default.xml`
 * - path: `move/move-default.xml`
 */
object MoveDefaultRenderer {

    fun renderTemplate(): String {
        return """
          |<catalog>
          |    <book id="b1">Clean Code</book>
          |</catalog>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "move/move-default.xml"
    }
}