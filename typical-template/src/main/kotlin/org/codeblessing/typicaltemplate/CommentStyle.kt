package org.codeblessing.typicaltemplate

data class CommentStyle(
    val startOfComment: String,
    val endOfComment: String,
    val isEndOfCommentEqualsEndOfLine : Boolean = false,
)
