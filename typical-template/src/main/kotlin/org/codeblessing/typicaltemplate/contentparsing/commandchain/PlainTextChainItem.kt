package org.codeblessing.typicaltemplate.contentparsing.commandchain

data class PlainTextChainItem(
    val text: String,
    val removeFirstLineIfWhitespaces: Boolean = false,
    val removeLastLineIfWhitespaces: Boolean = false,
): ChainItem {
    val textWithoutRemoveLines: String
        get() {
            val lines: MutableList<String> = text.lines().toMutableList()
            if(lines.isEmpty()) {
                return text
            }
            if (removeFirstLineIfWhitespaces && lines.first().isOnlyWhitespace()) {
                lines.removeFirst()
            }
            if(lines.isEmpty()) {
                return text
            }
            if (removeLastLineIfWhitespaces && lines.last().isOnlyWhitespace()) {
                lines.removeLast()
            }
            // here we do not preserve the original line separator
            return lines.joinToString("\n")
        }
    private fun String.isOnlyWhitespace(): Boolean {
        return this.all { it.isWhitespace() }
    }
}
