package org.codeblessing.typicaltemplate.contentparsing.fragmenter

import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

data class CommandFragment(
    override val lineNumbers: LineNumbers,
    val keywordCommand: KeywordCommand
) : TemplateFragment
