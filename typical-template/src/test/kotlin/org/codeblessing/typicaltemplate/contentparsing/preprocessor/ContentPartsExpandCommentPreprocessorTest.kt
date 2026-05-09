package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.commandchain.ContentPartBuilder
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ContentPartsExpandCommentPreprocessorTest {

    private val removeCommentCommandKeys = setOf(
        CommandKey.REMOVE_BLANKS_BEFORE_COMMENT,
        CommandKey.REMOVE_BLANKS_AFTER_COMMENT,
        CommandKey.REMOVE_BLANKS_AND_LINEBREAK_BEFORE_COMMENT,
        CommandKey.REMOVE_BLANKS_AND_LINEBREAK_AFTER_COMMENT,
    )

    @Test
    fun `empty list returns empty list`() {
        val input = ContentPartBuilder.create().build()

        val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

        assertEquals(emptyList<Any>(), result)
    }

    @Test
    fun `list without remove-comment command is returned unchanged`() {
        val input = ContentPartBuilder.create()
            .addText("some text")
            .addTemplateComment().addIfCommand().end()
            .addText("more text")
            .build()

        val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

        assertEquals(input, result)
    }

    @Nested
    inner class RemoveBlanksAfterComment {

        @Test
        fun `strips leading spaces from following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksAfterCommentCommand().end()
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
        fun `strips leading tabs from following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksAfterCommentCommand().end()
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
        fun `stops before line-ending`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksAfterCommentCommand().end()
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
        fun `with no leading blanks leaves following text unchanged`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksAfterCommentCommand().end()
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
        fun `removes text part when following text becomes empty`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksAfterCommentCommand().end()
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
    inner class RemoveBlanksAndLinebreakAfterComment {

        @Test
        fun `strips leading spaces and newline from following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksAndLinebreakAfterCommentCommand().end()
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
        fun `strips leading spaces and crlf from following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksAndLinebreakAfterCommentCommand().end()
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
        fun `strips only one newline`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksAndLinebreakAfterCommentCommand().end()
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
        fun `with no newline after blanks only strips blanks`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksAndLinebreakAfterCommentCommand().end()
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
    inner class RemoveBlanksBeforeComment {

        @Test
        fun `strips trailing spaces from preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello   ")
                .addTemplateComment().addRemoveBlanksBeforeCommentCommand().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `strips trailing tabs from preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello\t\t")
                .addTemplateComment().addRemoveBlanksBeforeCommentCommand().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `stops before line-ending`() {
            val input = ContentPartBuilder.create()
                .addText("Hello\n   ")
                .addTemplateComment().addRemoveBlanksBeforeCommentCommand().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello\n")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `with no trailing blanks leaves preceding text unchanged`() {
            val input = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().addRemoveBlanksBeforeCommentCommand().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `removes text part when preceding text becomes empty`() {
            val input = ContentPartBuilder.create()
                .addText("   ")
                .addTemplateComment().addRemoveBlanksBeforeCommentCommand().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }
    }

    @Nested
    inner class RemoveBlanksAndLinebreakBeforeComment {

        @Test
        fun `strips trailing spaces and newline from preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello\n   ")
                .addTemplateComment().addRemoveBlanksAndLinebreakBeforeCommentCommand().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `strips trailing spaces and crlf from preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello\r\n   ")
                .addTemplateComment().addRemoveBlanksAndLinebreakBeforeCommentCommand().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `strips only one newline`() {
            val input = ContentPartBuilder.create()
                .addText("Hello\n\n")
                .addTemplateComment().addRemoveBlanksAndLinebreakBeforeCommentCommand().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello\n")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `with no newline before blanks only strips blanks`() {
            val input = ContentPartBuilder.create()
                .addText("Hello   ")
                .addTemplateComment().addRemoveBlanksAndLinebreakBeforeCommentCommand().end()
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
        fun `after-comment with no following element keeps content in place but strips remove-comment command`() {
            val input = ContentPartBuilder.create()
                .addText("before")
                .addTemplateComment().addRemoveBlanksAfterCommentCommand().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("before")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `after-comment with following comment as neighbor keeps content in place but strips remove-comment command`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksAfterCommentCommand().end()
                .addTemplateComment().addIfCommand().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addTemplateComment().addIfCommand().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `before-comment with no preceding element keeps content in place but strips remove-comment command`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksBeforeCommentCommand().end()
                .addText("after")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("after")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `before-comment with preceding comment as neighbor keeps content in place but strips remove-comment command`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addIfCommand().end()
                .addTemplateComment().addRemoveBlanksBeforeCommentCommand().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().addIfCommand().end()
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }
    }

    @Nested
    inner class CommandRemoval {

        @Test
        fun `remove-comment command is removed from result when remove has an effect`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addRemoveBlanksAfterCommentCommand().end()
                .addText("   Hello")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            val remainingRemoveCommentCommands = result
                .filterIsInstance<TemplateCommentContentPart>()
                .flatMap { it.keywordCommands }
                .filter { it.commandKey in removeCommentCommandKeys }
            assertEquals(emptyList<Any>(), remainingRemoveCommentCommands)
        }

        @Test
        fun `remove-comment command is removed from result when remove has no effect`() {
            val input = ContentPartBuilder.create()
                .addText("before")
                .addTemplateComment().addRemoveBlanksAfterCommentCommand().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            val remainingRemoveCommentCommands = result
                .filterIsInstance<TemplateCommentContentPart>()
                .flatMap { it.keywordCommands }
                .filter { it.commandKey in removeCommentCommandKeys }
            assertEquals(emptyList<Any>(), remainingRemoveCommentCommands)
        }
    }
}
