package org.codeblessing.typicaltemplate.contentparsing.resolver

import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

sealed interface TemplateContentPart {
    val lineNumbers: LineNumbers
}
