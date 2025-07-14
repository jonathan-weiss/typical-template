package org.codeblessing.typicaltemplate.contentparsing

data class CommentStyle(
    val startOfComment: String,
    val endOfComment: String,
    val includeEndCommentInContent : Boolean = false,
)
