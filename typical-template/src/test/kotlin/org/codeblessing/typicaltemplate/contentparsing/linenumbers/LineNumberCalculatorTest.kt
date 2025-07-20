package org.codeblessing.typicaltemplate.contentparsing.linenumbers

import org.codeblessing.typicaltemplate.contentparsing.tokenizer.PlainContentToken
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.Token
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.TokenWithMetadata
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LineNumberCalculatorTest {

    val token1 = createTokenWithMetadata(
        value =
            """// line1
               // line2
               // line3
               // line4
            """
    )

    val token2 = createTokenWithMetadata(
        value =
            """// line5
               // line6
            """
    )

    val token3 = createTokenWithMetadata(
        value =
            """// line7
               // line8
               // line9
            """
    )
    val listOfTokens = listOf<TokenWithMetadata>(
        token1,
        token2,
        token3,
    )

    @Test
    fun `calculate line number of the first token`() {
        val lineNumbers = LineNumberCalculator.calculateLineNumbers(tokenWithMetadata = token1, allTokens = listOfTokens)

        assertEquals(1, lineNumbers.startLineNumber)
        assertEquals(4, lineNumbers.endLineNumber)
    }

    @Test
    fun `calculate line number of a token in between`() {
        val lineNumbers = LineNumberCalculator.calculateLineNumbers(tokenWithMetadata = token2, allTokens = listOfTokens)

        assertEquals(5, lineNumbers.startLineNumber)
        assertEquals(6, lineNumbers.endLineNumber)
    }

    @Test
    fun `calculate line number of the last token`() {
        val lineNumbers = LineNumberCalculator.calculateLineNumbers(tokenWithMetadata = token3, allTokens = listOfTokens)

        assertEquals(7, lineNumbers.startLineNumber)
        assertEquals(9, lineNumbers.endLineNumber)
    }

    @Test
    fun `calculate one-liner`() {
        val oneLinerToken = createTokenWithMetadata(value = "// one-line")


        val lineNumbers = LineNumberCalculator.calculateLineNumbers(tokenWithMetadata = oneLinerToken, allTokens = listOf(oneLinerToken))

        assertEquals(1, lineNumbers.startLineNumber)
        assertEquals(1, lineNumbers.endLineNumber)
    }

    private fun createTokenWithMetadata(value: String): TokenWithMetadata {
        return TokenWithMetadata(token =PlainContentToken(value = value), fullContent = value)
    }

}
