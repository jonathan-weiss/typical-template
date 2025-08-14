package org.codeblessing.typicaltemplate.filemapping

import org.codeblessing.typicaltemplate.CommentStyle
import org.codeblessing.typicaltemplate.CommentType

object CommentStyles {
    val HTML_COMMENT_STYLES = listOf(
        CommentStyle(
            startOfComment = "<!--",
            endOfComment = "-->",
            commentType = CommentType.BLOCK_COMMENT,
        ),
    )

    val KOTLIN_COMMENT_STYLES = listOf(
        CommentStyle(
            startOfComment = "/*",
            endOfComment = "*/",
            commentType = CommentType.BLOCK_COMMENT,
        ),
        CommentStyle(
            startOfComment = "//",
            endOfComment = null,
            commentType = CommentType.LINE_COMMENT,
        ),
    )

    val SCSS_COMMENT_STYLES = KOTLIN_COMMENT_STYLES
    val TYPESCRIPT_COMMENT_STYLES = KOTLIN_COMMENT_STYLES

}
