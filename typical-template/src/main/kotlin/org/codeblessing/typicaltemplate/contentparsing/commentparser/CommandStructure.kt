package org.codeblessing.typicaltemplate.contentparsing.commentparser

import org.codeblessing.typicaltemplate.KeywordType

data class CommandStructure(
    val keywordType: KeywordType,
    val keyword: String,
    val brackets: List<Map<String, String>>,
)
