/*
 * This file is generated using typical-template.
 */
package org.codeblessing.typicaltemplate.example.renderer



/**
 * Generate the content for the template `WhitespaceKotlinRenderer`.
 *
 * This template renderer was generated from the template:
 * - file: `Whitespace.kt`
 * - path: `my/example/businessproject/whitespace/Whitespace.kt`
 */
object WhitespaceKotlinRenderer {

    fun renderTemplate(): String {
        return """
          |val default = "keepA" +
          |   "keepB"
          |val rbb = "keepA" +"keepB"
          |val rba = "keepA"+ "keepB"
          |val rlb = "keepA" +"keepB"
          |val rla = "keepA" +    "keepB"
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "my/example/businessproject/whitespace/Whitespace.kt"
    }
}