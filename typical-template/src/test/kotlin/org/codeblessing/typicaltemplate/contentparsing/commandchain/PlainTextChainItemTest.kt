package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlainTextChainItemTest {

    @Test
    fun `text is unchanged if no remove markers are set`() {
        val multilineTextWithWhitespaces = "  \t \nand here is text\n \t  "

        val item = PlainTextChainItem(
            text = multilineTextWithWhitespaces,
            removeFirstLineIfWhitespaces = false,
            removeLastLineIfWhitespaces = false,
        )

        assertEquals(multilineTextWithWhitespaces, item.textWithoutRemoveLines)
    }

    @Test
    fun `text is unchanged if no whitespaces are in the text even if markers are set`() {
        val multilineTextWithoutWhitespaces = "hello\nand here is text\nwithout whitespaces"

        val item = PlainTextChainItem(
            text = multilineTextWithoutWhitespaces,
            removeFirstLineIfWhitespaces = true,
            removeLastLineIfWhitespaces = true,
        )

        assertEquals(multilineTextWithoutWhitespaces, item.textWithoutRemoveLines)
    }

    @Test
    fun `text is unchanged if text is empty even if markers are set`() {
        val multilineTextWithoutWhitespaces = ""

        val item = PlainTextChainItem(
            text = multilineTextWithoutWhitespaces,
            removeFirstLineIfWhitespaces = true,
            removeLastLineIfWhitespaces = true,
        )

        assertEquals(multilineTextWithoutWhitespaces, item.textWithoutRemoveLines)
    }

    @Test
    fun `last line with whitespace is removed if remove marker is set`() {
        val item = PlainTextChainItem(
            text = "  \t \nand here is text\n \t  ",
            removeFirstLineIfWhitespaces = false,
            removeLastLineIfWhitespaces = true,
        )

        assertEquals("  \t \nand here is text", item.textWithoutRemoveLines)
    }

    @Test
    fun `first line with whitespace is removed if remove marker is set`() {
        val item = PlainTextChainItem(
            text = "  \t \nand here is text\n \t  ",
            removeFirstLineIfWhitespaces = true,
            removeLastLineIfWhitespaces = false,
        )

        assertEquals("and here is text\n \t  ", item.textWithoutRemoveLines)
    }

    @Test
    fun `first line and last line with whitespace is removed if remove markers are set`() {
        val item = PlainTextChainItem(
            text = "  \t \nand here is text\n \t  ",
            removeFirstLineIfWhitespaces = true,
            removeLastLineIfWhitespaces = true,
        )

        assertEquals("and here is text", item.textWithoutRemoveLines)
    }

}
