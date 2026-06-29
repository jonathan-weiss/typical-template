/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer



/**
 * Generate the content for the template `MoveRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `move.html`
 * - path: `move/move.html`
 */
object MoveRenderer {

    fun renderTemplate(): String {
        return """
          |<!DOCTYPE html>
          |<html lang="en">
          |<body>
          |    <ul>
          |        <li lang='en' id="b1">Clean Code</li>
          |        <li id="b2"><span>Refactoring</span></li>
          |        <li><!-- a plain html comment the move below travels across -->moved-across-normal-comment:VALUE</li>
          |        <li>x,y,z,(last)w,UNREACHABLE</li>
          |    </ul>
          |</body>
          |</html>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "move/move.html"
    }
}