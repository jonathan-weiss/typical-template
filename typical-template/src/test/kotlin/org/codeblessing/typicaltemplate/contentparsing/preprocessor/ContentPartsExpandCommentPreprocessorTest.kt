package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.DirectionValue.BACKWARD
import org.codeblessing.typicaltemplate.DirectionValue.FORWARD
import org.codeblessing.typicaltemplate.ExpandModeValue.BLANKS
import org.codeblessing.typicaltemplate.ExpandModeValue.LINEBREAK
import org.codeblessing.typicaltemplate.contentparsing.commandchain.ContentPartBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ContentPartsExpandCommentPreprocessorTest {

    @Test
    fun `empty list returns empty list`() {
        val input = ContentPartBuilder.create().build()

        val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

        assertEquals(emptyList<Any>(), result)
    }

    @Test
    fun `list without expand-comment command is returned unchanged`() {
        val input = ContentPartBuilder.create()
            .addText("some text")
            .addTemplateComment().addIfCommand().end()
            .addText("more text")
            .build()

        val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

        assertEquals(input, result)
    }

    @Nested
    inner class ForwardBlanks {

        @Test
        fun `forward blanks strips leading spaces from following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addExpandCommentCommand(direction = FORWARD, stripMode = BLANKS).end()
                .addText("   Hello")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("Hello")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward blanks strips leading tabs from following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addExpandCommentCommand(direction = FORWARD, stripMode = BLANKS).end()
                .addText("\t\tHello")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("Hello")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward blanks stops before line-ending`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addExpandCommentCommand(direction = FORWARD, stripMode = BLANKS).end()
                .addText("   \nHello")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("\nHello")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward blanks with no leading blanks leaves following text unchanged`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addExpandCommentCommand(direction = FORWARD, stripMode = BLANKS).end()
                .addText("Hello")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("Hello")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward blanks removes text part when following text becomes empty`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addExpandCommentCommand(direction = FORWARD, stripMode = BLANKS).end()
                .addText("   ")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }
    }

    @Nested
    inner class ForwardLinebreak {

        @Test
        fun `forward linebreak strips leading spaces and newline from following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addExpandCommentCommand(direction = FORWARD, stripMode = LINEBREAK).end()
                .addText("   \nHello")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("Hello")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward linebreak strips leading spaces and crlf from following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addExpandCommentCommand(direction = FORWARD, stripMode = LINEBREAK).end()
                .addText("   \r\nHello")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("Hello")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward linebreak strips only one newline`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addExpandCommentCommand(direction = FORWARD, stripMode = LINEBREAK).end()
                .addText("\n\nHello")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("\nHello")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward linebreak with no newline after blanks only strips blanks`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addExpandCommentCommand(direction = FORWARD, stripMode = LINEBREAK).end()
                .addText("   Hello")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("Hello")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }
    }

    @Nested
    inner class BackwardBlanks {

        @Test
        fun `backward blanks strips trailing spaces from preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello   ")
                .addTemplateComment().addExpandCommentCommand(direction = BACKWARD, stripMode = BLANKS).end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `backward blanks strips trailing tabs from preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello\t\t")
                .addTemplateComment().addExpandCommentCommand(direction = BACKWARD, stripMode = BLANKS).end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `backward blanks stops before line-ending`() {
            val input = ContentPartBuilder.create()
                .addText("Hello\n   ")
                .addTemplateComment().addExpandCommentCommand(direction = BACKWARD, stripMode = BLANKS).end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello\n")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `backward blanks with no trailing blanks leaves preceding text unchanged`() {
            val input = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().addExpandCommentCommand(direction = BACKWARD, stripMode = BLANKS).end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `backward blanks removes text part when preceding text becomes empty`() {
            val input = ContentPartBuilder.create()
                .addText("   ")
                .addTemplateComment().addExpandCommentCommand(direction = BACKWARD, stripMode = BLANKS).end()
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }
    }

    @Nested
    inner class BackwardLinebreak {

        @Test
        fun `backward linebreak strips trailing spaces and newline from preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello\n   ")
                .addTemplateComment().addExpandCommentCommand(direction = BACKWARD, stripMode = LINEBREAK).end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `backward linebreak strips trailing spaces and crlf from preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello\r\n   ")
                .addTemplateComment().addExpandCommentCommand(direction = BACKWARD, stripMode = LINEBREAK).end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `backward linebreak strips only one newline`() {
            val input = ContentPartBuilder.create()
                .addText("Hello\n\n")
                .addTemplateComment().addExpandCommentCommand(direction = BACKWARD, stripMode = LINEBREAK).end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello\n")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `backward linebreak with no newline before blanks only strips blanks`() {
            val input = ContentPartBuilder.create()
                .addText("Hello   ")
                .addTemplateComment().addExpandCommentCommand(direction = BACKWARD, stripMode = LINEBREAK).end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }
    }

    @Nested
    inner class DoNothingCases {

        @Test
        fun `forward with no following element leaves comment unchanged`() {
            val input = ContentPartBuilder.create()
                .addText("before")
                .addTemplateComment().addExpandCommentCommand(direction = FORWARD, stripMode = BLANKS).end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }

        @Test
        fun `forward with following comment as neighbor leaves comment unchanged`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addExpandCommentCommand(direction = FORWARD, stripMode = BLANKS).end()
                .addTemplateComment().addIfCommand().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }

        @Test
        fun `backward with no preceding element leaves comment unchanged`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addExpandCommentCommand(direction = BACKWARD, stripMode = BLANKS).end()
                .addText("after")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }

        @Test
        fun `backward with preceding comment as neighbor leaves comment unchanged`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addIfCommand().end()
                .addTemplateComment().addExpandCommentCommand(direction = BACKWARD, stripMode = BLANKS).end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }
    }
}
