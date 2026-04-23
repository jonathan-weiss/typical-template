package org.codeblessing.typicaltemplate.contentparsing.tokenizer

data class RawContentPart(
    val contentType: ContentType,
    val content: String,
    val pristineContent: String,
)
