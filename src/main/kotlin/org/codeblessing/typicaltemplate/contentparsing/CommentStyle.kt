package org.codeblessing.typicaltemplate.contentparsing

data class CommentStyle(
    val startOfCommentRegex: String,
    val endOfCommentRegex: String,
    val includeEndCommentInContent : Boolean = false,
)
