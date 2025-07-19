package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers.Companion.EMPTY_LINE_NUMBERS

class TemplateParsingException(
    val lineNumbers: LineNumbers = EMPTY_LINE_NUMBERS,
    val msg: String,
    cause: Exception? = null
): RuntimeException(msg, cause) {
    companion object {
        fun <T> reThrowWithLineNumbers(lineNumbers: LineNumbers, block: () -> T): T {
            try {
                return block()
            } catch (e: TemplateParsingException) {
                throw TemplateParsingException(lineNumbers = lineNumbers, msg = e.msg, cause = e)
            }
        }
    }
}


