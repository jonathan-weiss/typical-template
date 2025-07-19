package org.codeblessing.typicaltemplate.filemapping

import org.codeblessing.typicaltemplate.CommentStyle

object CommentStyles {
    val HTML_COMMENT_STYLES = listOf(
        CommentStyle(
            startOfComment = "<!--",
            endOfComment = "-->"
        ),
    )

    val KOTLIN_COMMENT_STYLES = listOf(
        CommentStyle(
            startOfComment = "/*",
            endOfComment = "*/"
        ),
        CommentStyle(
            startOfComment = "//",
            endOfComment = "",
            isEndOfCommentEqualsEndOfLine = true
        ),
    )

}
