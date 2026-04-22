package org.codeblessing.typicaltemplate.contentparsing.commentparser

data class StructuredKeyword(
    val keyword: String,
    val brackets: List<Map<String, String>>,
    val keywordType: TemplateCommentParser.KeywordType,
)
