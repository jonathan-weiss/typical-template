package org.codeblessing.typicaltemplate.contentparsing.linenumbers

import org.codeblessing.typicaltemplate.contentparsing.tokenizer.Token

object LineNumberCalculator {

    fun calculateLineNumbers(
        token: Token,
        allTokens: List<Token>
    ): LineNumbers {

        var previousTokenEndLineNumber = 0

        for(currentToken in allTokens) {
            if(currentToken != token) {
                previousTokenEndLineNumber += currentToken.value.countLines()
            } else {
                break
            }
        }

        val tokenStartLineNumber = previousTokenEndLineNumber + 1
        val tokenEndLineNumber = (previousTokenEndLineNumber + token.value.countLines())
            .coerceAtLeast(tokenStartLineNumber)

        return LineNumbers(
            startLineNumber = tokenStartLineNumber,
            endLineNumber = tokenEndLineNumber,
            context = token.value,
            formattedDescription = "Lines ${tokenStartLineNumber}-${tokenEndLineNumber}: '${token.value}'",
        )
    }

    private fun String.countLines(): Int {
        // TODO add support for "\r", "\r\n"
        return this.count { it == '\n' }
    }
}
