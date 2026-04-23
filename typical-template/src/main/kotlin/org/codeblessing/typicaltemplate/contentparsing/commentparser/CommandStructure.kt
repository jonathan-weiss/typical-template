package org.codeblessing.typicaltemplate.contentparsing.commentparser

data class CommandStructure(
    val keywordType: KeywordType,
    val keyword: String,
    val brackets: List<Map<String, String>>,
)
