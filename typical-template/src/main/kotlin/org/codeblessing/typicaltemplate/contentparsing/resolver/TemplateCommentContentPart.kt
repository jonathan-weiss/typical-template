package org.codeblessing.typicaltemplate.contentparsing.resolver

import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

data class TemplateCommentContentPart(
    override val lineNumbers: LineNumbers,
    val keywordCommands: List<KeywordCommand>
) : TemplateContentPart
