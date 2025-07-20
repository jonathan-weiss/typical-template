package org.codeblessing.typicaltemplate.contentparsing.linenumbers

import org.codeblessing.typicaltemplate.contentparsing.tokenizer.TokenWithMetadata

object LineNumberCalculator {

    fun calculateLineNumbers(
        tokenWithMetadata: TokenWithMetadata,
        allTokens: List<TokenWithMetadata>
    ): LineNumbers {

        var previousTokenEndLineNumber = 0

        for(currentToken in allTokens) {
            if(currentToken != tokenWithMetadata) {
                previousTokenEndLineNumber += currentToken.fullContent.countLines()
            } else {
                break
            }
        }

        val tokenStartLineNumber = previousTokenEndLineNumber + 1
        val tokenEndLineNumber = (previousTokenEndLineNumber + tokenWithMetadata.fullContent.countLines())
            .coerceAtLeast(tokenStartLineNumber)

        return LineNumbers(
            startLineNumber = tokenStartLineNumber,
            endLineNumber = tokenEndLineNumber,
            context = tokenWithMetadata.fullContent,
            formattedDescription = "Lines ${tokenStartLineNumber}-${tokenEndLineNumber}: '${tokenWithMetadata.fullContent}'",
        )
    }

    private fun String.countLines(): Int {
        // TODO add support for "\r", "\r\n"
        return this.count { it == '\n' }
    }
}
