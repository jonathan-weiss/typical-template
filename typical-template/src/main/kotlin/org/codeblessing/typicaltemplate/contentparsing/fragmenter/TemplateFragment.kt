package org.codeblessing.typicaltemplate.contentparsing.fragmenter

import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

sealed interface TemplateFragment {
    val lineNumbers: LineNumbers
}
