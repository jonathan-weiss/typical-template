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
    inner class DefaultWhitespaceHandling {

        @Test
        fun `standalone comment line is removed entirely`() {
            val input = ContentPartBuilder.create()
                .addText("line1\n   ")
                .addTemplateComment().end()
                .addText("   \nline2")
                .build()

            val expected = ContentPartBuilder.create()
                .addText("line1\n")
                .addTemplateComment().end()
                .addText("line2")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `leading blanks are removed but the line break is kept`() {
            val input = ContentPartBuilder.create()
                .addText("line1\n\t  ")
                .addTemplateComment().end()
                .addText("  \nrest")
                .build()

            val expected = ContentPartBuilder.create()
                .addText("line1\n")
                .addTemplateComment().end()
                .addText("rest")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `trailing blanks and the crlf line break are removed`() {
            val input = ContentPartBuilder.create()
                .addText("text\n")
                .addTemplateComment().end()
                .addText("  \r\nrest")
                .build()

            val expected = ContentPartBuilder.create()
                .addText("text\n")
                .addTemplateComment().end()
                .addText("rest")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `with text before the comment on the same line nothing is removed`() {
            val input = ContentPartBuilder.create()
                .addText("foo ")
                .addTemplateComment().end()
                .addText("   \nbar")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }

        @Test
        fun `with non-blank after the comment on the same line nothing is removed`() {
            val input = ContentPartBuilder.create()
                .addText("   ")
                .addTemplateComment().end()
                .addText(" bar\n")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }

        @Test
        fun `comment at start of content strips following blanks and line break`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("   \nrest")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("rest")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `comment at end of content strips preceding blanks up to line start`() {
            val input = ContentPartBuilder.create()
                .addText("rest\n   ")
                .addTemplateComment().end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("rest\n")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `comment followed only by blanks without a line break strips those blanks`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("   ")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `default handling also applies to a comment carrying other commands`() {
            val input = ContentPartBuilder.create()
                .addText("   ")
                .addTemplateComment().addIfCommand().end()
                .addText("   \nbody")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().addIfCommand().end()
                .addText("body")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `several comments separated by blanks on the same line are treated as one comment`() {
            val input = ContentPartBuilder.create()
                .addText("a\n   ")
                .addTemplateComment().addIfCommand().end()
                .addText("  ")
                .addTemplateComment().addEndIfCommand().end()
                .addText("   \nb")
                .build()

            val expected = ContentPartBuilder.create()
                .addText("a\n")
                .addTemplateComment().addIfCommand().end()
                .addTemplateComment().addEndIfCommand().end()
                .addText("b")
                .build()

            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `comments separated by non-blank text on the same line are not grouped`() {
            val input = ContentPartBuilder.create()
                .addText("   ")
                .addTemplateComment().end()
                .addText(" X ")
                .addTemplateComment().end()
                .addText("   \n")
                .build()

            // Neither comment is standalone: the first has non-blank text after it on the line,
            // the second has non-blank text before it. Nothing is removed.
            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }

        @Test
        fun `grouped comments with non-blank after the last comment are left untouched`() {
            val input = ContentPartBuilder.create()
                .addText("   ")
                .addTemplateComment().end()
                .addText("   ")
                .addTemplateComment().end()
                .addText(" X\n")
                .build()

            // The two comments sit on the same line (only blanks in between) and are treated as
            // one comment. Because non-blank text follows the group, nothing is removed - the
            // blanks between the comments are kept too.
            val result = ContentPartsExpandCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }

        @Test
        fun `consecutive comments on separate lines are each trimmed individually`() {
            val input = ContentPartBuilder.create()
                .addText("   ")
                .addTemplateComment().end()
                .addText("   \n   ")
                .addTemplateComment().end()
                .addText("   \nend")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addTemplateComment().end()
                .addText("end")
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
