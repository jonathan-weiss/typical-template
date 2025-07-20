package org.codeblessing.typicaltemplate.contentparsing.tokenizer

data class TokenWithMetadata(
    val token: Token,
    val fullContent: String,
)
