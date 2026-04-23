package org.codeblessing.typicaltemplate.contentparsing.linenumbers

import org.codeblessing.typicaltemplate.contentparsing.tokenizer.PlainTextContentPart
import org.codeblessing.typicaltemplate.contentparsing.tokenizer.ContentPartWithMetadata
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LineNumberCalculatorTest {

    val contentPart1 = createContentPart(
        value =
            """// line1
               // line2
               // line3
               // line4
            """
    )

    val contentPart2 = createContentPart(
        value =
            """// line5
               // line6
            """
    )

    val contentPart3 = createContentPart(
        value =
            """// line7
               // line8
               // line9
            """
    )
    val listOfContentParts = listOf(
        contentPart1,
        contentPart2,
        contentPart3,
    )

    @Test
    fun `calculate line number of the first content part`() {
        val lineNumbers = LineNumberCalculator.calculateLineNumbers(contentPart = contentPart1, allContentParts = listOfContentParts)

        assertEquals(1, lineNumbers.startLineNumber)
        assertEquals(4, lineNumbers.endLineNumber)
    }

    @Test
    fun `calculate line number of a content part in between`() {
        val lineNumbers = LineNumberCalculator.calculateLineNumbers(contentPart = contentPart2, allContentParts = listOfContentParts)

        assertEquals(5, lineNumbers.startLineNumber)
        assertEquals(6, lineNumbers.endLineNumber)
    }

    @Test
    fun `calculate line number of the last content part`() {
        val lineNumbers = LineNumberCalculator.calculateLineNumbers(contentPart = contentPart3, allContentParts = listOfContentParts)

        assertEquals(7, lineNumbers.startLineNumber)
        assertEquals(9, lineNumbers.endLineNumber)
    }

    @Test
    fun `calculate one-liner`() {
        val oneLiner = createContentPart(value = "// one-line")


        val lineNumbers = LineNumberCalculator.calculateLineNumbers(contentPart = oneLiner, allContentParts = listOf(oneLiner))

        assertEquals(1, lineNumbers.startLineNumber)
        assertEquals(1, lineNumbers.endLineNumber)
    }

    private fun createContentPart(value: String): ContentPartWithMetadata {
        return ContentPartWithMetadata(contentPart =PlainTextContentPart(value = value), fullContent = value)
    }

}
