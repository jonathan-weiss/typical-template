/*
 * This file is generated using typical-template.
 */
package com.example



/**
 * Generate the content for the template MyRenderer filled up
 * with the content of the passed models.
 */
object MyRenderer {

    fun renderTemplate(): String {
        return """
          |
          |<div>Hello</div>
          |<div>World</div>
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(): String {
      return "input/my-renderer.html"
    }
}