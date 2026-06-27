/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer



/**
 * Generate the content for the template `MoveRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `move.xml`
 * - path: `move/move.xml`
 */
object MoveRenderer {

    fun renderTemplate(): String {
        return """
          |<?xml version="1.0" encoding="UTF-8"?>
          |<catalog>
          |    <book lang='en' id="b1">Clean Code</book>
          |    <book id="b2"><keep>Refactoring</keep></book>
          |    <row><!-- a plain xml comment the move below travels across -->moved-across-normal-comment:VALUE</row>
          |    <tags>x,y,z,(last)w,UNREACHABLE</tags>
          |</catalog>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "move/move.xml"
    }
}