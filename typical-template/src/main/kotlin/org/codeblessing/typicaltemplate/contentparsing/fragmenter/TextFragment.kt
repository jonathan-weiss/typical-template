package org.codeblessing.typicaltemplate.contentparsing.fragmenter

import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

data class TextFragment(
    override val lineNumbers: LineNumbers,
    val text: String
) : TemplateFragment
