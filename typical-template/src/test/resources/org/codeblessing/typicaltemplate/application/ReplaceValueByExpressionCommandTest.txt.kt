/*
 * This file is generated using typical-template.
 */
package com.example

import com.example.model.Person

/**
 * Generate the content for the template MyRenderer filled up
 * with the content of the passed models.
 */
object MyRenderer {

    fun renderTemplate(person: Person): String {
        return """
          |
          |
          |
          |<div>Hello ${person.name}</div>
          |
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(person: Person): String {
      return "input/my-renderer.html"
    }
}