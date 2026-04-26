package org.codeblessing.typicaltemplate.contentparsing.resolver

import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

data class TextContentPart(
    override val lineNumbers: LineNumbers,
    val text: String
) : TemplateContentPart
