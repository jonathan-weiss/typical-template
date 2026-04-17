package org.codeblessing.typicaltemplate.config

import org.codeblessing.typicaltemplate.CommentStyle

data class TypicalTemplateConfig(
    val namedCommentStyles: Map<String, CommentStyle>,
    val fileExtensionCommentStyles: List<FileExtensionCommentStyles>,
)
