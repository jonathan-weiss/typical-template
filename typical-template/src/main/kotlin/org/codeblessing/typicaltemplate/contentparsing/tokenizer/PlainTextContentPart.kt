package org.codeblessing.typicaltemplate.contentparsing.tokenizer

data class PlainTextContentPart(
    override val value: String,
) : ContentPart
