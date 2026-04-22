package org.codeblessing.typicaltemplate.contentparsing.commentparser

data class StructuredKeyword(
    val keywordType: KeywordType,
    val keyword: String,
    val brackets: List<Map<String, String>>,
)
