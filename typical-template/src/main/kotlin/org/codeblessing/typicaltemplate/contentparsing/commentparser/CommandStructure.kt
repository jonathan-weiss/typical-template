package org.codeblessing.typicaltemplate.contentparsing.commentparser

data class CommandStructure(
    val keyword: String,
    val brackets: List<Map<String, String>>,
)
