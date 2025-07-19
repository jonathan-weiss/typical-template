package org.codeblessing.typicaltemplate.contentparsing.commentparser

data class StructuredComment(
    val keyword: String,
    val brackets: List<Map<String, String>>,
)
