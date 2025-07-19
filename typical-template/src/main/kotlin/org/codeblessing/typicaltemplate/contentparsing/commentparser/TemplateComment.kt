package org.codeblessing.typicaltemplate.contentparsing.commentparser

data class TemplateComment(
    val keyword: String,
    val brackets: List<Map<String, String>>,
)
