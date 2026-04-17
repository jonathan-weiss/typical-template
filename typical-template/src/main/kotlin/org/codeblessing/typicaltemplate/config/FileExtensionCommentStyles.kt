package org.codeblessing.typicaltemplate.config

import org.codeblessing.typicaltemplate.CommentStyle

data class FileExtensionCommentStyles(
    val extension: String,
    val commentStyles: List<CommentStyle>,
)
