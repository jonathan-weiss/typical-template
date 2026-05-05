package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.DirectionValue.BACKWARD
import org.codeblessing.typicaltemplate.DirectionValue.FORWARD
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingErrorCode
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.commandchain.ContentPartBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ContentPartsMoveCommentPreprocessorTest {

    @Test
    fun `empty list returns empty list`() {
        val input = ContentPartBuilder.create().build()

        val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

        assertEquals(emptyList<Any>(), result)
    }

    @Test
    fun `list without move-comment command is returned unchanged`() {
        val input = ContentPartBuilder.create()
            .addText("some text")
            .addTemplateComment().addIfCommand().end()
            .addText("more text")
            .build()

        val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

        assertEquals(input, result)
    }

    @Nested
    inner class ForwardDirection {

        @Test
        fun `forward simple swap moves comment after following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD).end()
                .addText("Hello World")
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello World")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward with no following element leaves comment in place`() {
            val input = ContentPartBuilder.create()
                .addText("before")
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD).end()
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }

        @Test
        fun `forward with following comment as neighbor leaves comment in place`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD).end()
                .addTemplateComment().addIfCommand().end()
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }

        @Test
        fun `forward beforeFirstOccurrenceOf places comment before first occurrence in following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD, beforeFirstOccurrenceOf = "World").end()
                .addText("Hello World Bye World")
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello ")
                .addTemplateComment().end()
                .addText("World Bye World")
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward afterFirstOccurrenceOf places comment after first occurrence in following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD, afterFirstOccurrenceOf = "Hello").end()
                .addText("Hello World Hello")
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .addText(" World Hello")
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward beforeLastOccurrenceOf places comment before last occurrence in following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD, beforeLastOccurrenceOf = "World").end()
                .addText("Hello World Bye World End")
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello World Bye ")
                .addTemplateComment().end()
                .addText("World End")
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward afterLastOccurrenceOf places comment after last occurrence in following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD, afterLastOccurrenceOf = "World").end()
                .addText("Hello World Bye World End")
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello World Bye World")
                .addTemplateComment().end()
                .addText(" End")
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward beforeFirstOccurrenceOf at start of text produces no left text part`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD, beforeFirstOccurrenceOf = "Hello").end()
                .addText("Hello World")
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("Hello World")
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `forward afterLastOccurrenceOf at end of text produces no right text part`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD, afterLastOccurrenceOf = "World").end()
                .addText("Hello World")
                .build()

            // "World" ends at index 11 (end of string), so right part is empty and omitted
            val expected = ContentPartBuilder.create()
                .addText("Hello World")
                .addTemplateComment().end()
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }
    }

    @Nested
    inner class BackwardDirection {

        @Test
        fun `backward simple swap moves comment before preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello World")
                .addTemplateComment().addMoveCommentCommand(direction = BACKWARD).end()
                .build()

            val expected = ContentPartBuilder.create()
                .addTemplateComment().end()
                .addText("Hello World")
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `backward with no preceding element leaves comment in place`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = BACKWARD).end()
                .addText("after")
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }

        @Test
        fun `backward with preceding comment as neighbor leaves comment in place`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addIfCommand().end()
                .addTemplateComment().addMoveCommentCommand(direction = BACKWARD).end()
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(input, result)
        }

        @Test
        fun `backward beforeFirstOccurrenceOf places comment before first occurrence in preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello World Bye World")
                .addTemplateComment().addMoveCommentCommand(direction = BACKWARD, beforeFirstOccurrenceOf = "World").end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello ")
                .addTemplateComment().end()
                .addText("World Bye World")
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `backward afterFirstOccurrenceOf places comment after first occurrence in preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello World Hello")
                .addTemplateComment().addMoveCommentCommand(direction = BACKWARD, afterFirstOccurrenceOf = "Hello").end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello")
                .addTemplateComment().end()
                .addText(" World Hello")
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `backward beforeLastOccurrenceOf places comment before last occurrence in preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello World Bye World End")
                .addTemplateComment().addMoveCommentCommand(direction = BACKWARD, beforeLastOccurrenceOf = "World").end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello World Bye ")
                .addTemplateComment().end()
                .addText("World End")
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }

        @Test
        fun `backward afterLastOccurrenceOf places comment after last occurrence in preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello World Bye World End")
                .addTemplateComment().addMoveCommentCommand(direction = BACKWARD, afterLastOccurrenceOf = "World").end()
                .build()

            val expected = ContentPartBuilder.create()
                .addText("Hello World Bye World")
                .addTemplateComment().end()
                .addText(" End")
                .build()

            val result = ContentPartsMoveCommentPreprocessor.runPreprocessing(input)

            assertEquals(expected, result)
        }
    }

    @Nested
    inner class ExceptionCases {

        @Test
        fun `forward beforeFirstOccurrenceOf throws when string not found in following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD, beforeFirstOccurrenceOf = "Missing").end()
                .addText("Hello World")
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                ContentPartsMoveCommentPreprocessor.runPreprocessing(input)
            }
            assertEquals(TemplateParsingErrorCode.SEARCH_TOKEN_NOT_FOUND, exception.errorCode)
        }

        @Test
        fun `forward afterFirstOccurrenceOf throws when string not found in following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD, afterFirstOccurrenceOf = "Missing").end()
                .addText("Hello World")
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                ContentPartsMoveCommentPreprocessor.runPreprocessing(input)
            }
            assertEquals(TemplateParsingErrorCode.SEARCH_TOKEN_NOT_FOUND, exception.errorCode)
        }

        @Test
        fun `forward beforeLastOccurrenceOf throws when string not found in following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD, beforeLastOccurrenceOf = "Missing").end()
                .addText("Hello World")
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                ContentPartsMoveCommentPreprocessor.runPreprocessing(input)
            }
            assertEquals(TemplateParsingErrorCode.SEARCH_TOKEN_NOT_FOUND, exception.errorCode)
        }

        @Test
        fun `forward afterLastOccurrenceOf throws when string not found in following text`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment().addMoveCommentCommand(direction = FORWARD, afterLastOccurrenceOf = "Missing").end()
                .addText("Hello World")
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                ContentPartsMoveCommentPreprocessor.runPreprocessing(input)
            }
            assertEquals(TemplateParsingErrorCode.SEARCH_TOKEN_NOT_FOUND, exception.errorCode)
        }

        @Test
        fun `backward beforeFirstOccurrenceOf throws when string not found in preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello World")
                .addTemplateComment().addMoveCommentCommand(direction = BACKWARD, beforeFirstOccurrenceOf = "Missing").end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                ContentPartsMoveCommentPreprocessor.runPreprocessing(input)
            }
            assertEquals(TemplateParsingErrorCode.SEARCH_TOKEN_NOT_FOUND, exception.errorCode)
        }

        @Test
        fun `backward afterLastOccurrenceOf throws when string not found in preceding text`() {
            val input = ContentPartBuilder.create()
                .addText("Hello World")
                .addTemplateComment().addMoveCommentCommand(direction = BACKWARD, afterLastOccurrenceOf = "Missing").end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                ContentPartsMoveCommentPreprocessor.runPreprocessing(input)
            }
            assertEquals(TemplateParsingErrorCode.SEARCH_TOKEN_NOT_FOUND, exception.errorCode)
        }
    }
}
