package org.codeblessing.typicaltemplate

data class CommentStyle(
    val startOfComment: String,
    val endOfComment: String?, // not necessary for line comments
    val commentType: CommentType,
)

enum class CommentType {
    BLOCK_COMMENT,
    LINE_COMMENT,
}
