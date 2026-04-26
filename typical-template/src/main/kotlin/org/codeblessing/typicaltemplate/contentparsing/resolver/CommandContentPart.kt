package org.codeblessing.typicaltemplate.contentparsing.resolver

import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

data class CommandContentPart(
    override val lineNumbers: LineNumbers,
    val keywordCommand: KeywordCommand
) : TemplateContentPart
