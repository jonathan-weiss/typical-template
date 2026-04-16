/*
 * This file is generated using typical-template.
 */
package com.example

import com.example.model.Team

/**
 * Generate the content for the template MyRenderer filled up
 * with the content of the passed models.
 */
object MyRenderer {

    fun renderTemplate(team: Team): String {
        return """
          |
          |
          |<html>
          |${ team.members.joinToString("") { theMember ->  """
              |
              |
              |<div>Member: ${theMember}</div>
              |
              |
          """ } }
          |</html>
        """.trimMargin(marginPrefix = "|")
    }

    fun filePath(team: Team): String {
      return "input/my-renderer.html"
    }
}