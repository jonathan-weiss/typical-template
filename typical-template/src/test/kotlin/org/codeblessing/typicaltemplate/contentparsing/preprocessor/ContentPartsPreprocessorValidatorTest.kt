package org.codeblessing.typicaltemplate.contentparsing.preprocessor

import org.codeblessing.typicaltemplate.DirectionValue.BACKWARD
import org.codeblessing.typicaltemplate.DirectionValue.FORWARD
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.commandchain.ContentPartBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ContentPartsPreprocessorValidatorTest {

    @Test
    fun `empty list passes validation and is returned unchanged`() {
        val input = ContentPartBuilder.create().build()

        val result = ContentPartsPreprocessorValidator.validatePreprocessing(input)

        assertEquals(input, result)
    }

    @Test
    fun `comment with no move-comment command passes validation`() {
        val input = ContentPartBuilder.create()
            .addTemplateComment().addIfCommand().end()
            .build()

        val result = ContentPartsPreprocessorValidator.validatePreprocessing(input)

        assertEquals(input, result)
    }

    @Test
    fun `comment with exactly one move-comment command passes validation`() {
        val input = ContentPartBuilder.create()
            .addTemplateComment().addMoveCommentCommand(direction = FORWARD).end()
            .build()

        val result = ContentPartsPreprocessorValidator.validatePreprocessing(input)

        assertEquals(input, result)
    }

    @Test
    fun `text parts are not validated and pass through unchanged`() {
        val input = ContentPartBuilder.create()
            .addText("some text")
            .addText("more text")
            .build()

        val result = ContentPartsPreprocessorValidator.validatePreprocessing(input)

        assertEquals(input, result)
    }

    @Test
    fun `comment with two move-comment commands throws exception`() {
        val input = ContentPartBuilder.create()
            .addTemplateComment()
            .addMoveCommentCommand(direction = FORWARD)
            .addMoveCommentCommand(direction = BACKWARD)
            .end()
            .build()

        assertThrows(TemplateParsingException::class.java) {
            ContentPartsPreprocessorValidator.validatePreprocessing(input)
        }
    }

    @Test
    fun `comment with three move-comment commands throws exception`() {
        val input = ContentPartBuilder.create()
            .addTemplateComment()
            .addMoveCommentCommand(direction = FORWARD)
            .addMoveCommentCommand(direction = FORWARD)
            .addMoveCommentCommand(direction = BACKWARD)
            .end()
            .build()

        assertThrows(TemplateParsingException::class.java) {
            ContentPartsPreprocessorValidator.validatePreprocessing(input)
        }
    }

    @Test
    fun `multiple comments each with at most one move-comment command passes validation`() {
        val input = ContentPartBuilder.create()
            .addTemplateComment().addMoveCommentCommand(direction = FORWARD).end()
            .addText("text")
            .addTemplateComment().addMoveCommentCommand(direction = BACKWARD).end()
            .build()

        val result = ContentPartsPreprocessorValidator.validatePreprocessing(input)

        assertEquals(input, result)
    }

    @Test
    fun `second comment with two move-comment commands throws exception`() {
        val input = ContentPartBuilder.create()
            .addTemplateComment().addMoveCommentCommand(direction = FORWARD).end()
            .addText("text")
            .addTemplateComment()
            .addMoveCommentCommand(direction = FORWARD)
            .addMoveCommentCommand(direction = BACKWARD)
            .end()
            .build()

        assertThrows(TemplateParsingException::class.java) {
            ContentPartsPreprocessorValidator.validatePreprocessing(input)
        }
    }

    @Test
    fun `mixed text and comments where valid comment has one move-comment command passes validation`() {
        val input = ContentPartBuilder.create()
            .addText("before")
            .addTemplateComment().addMoveCommentCommand(direction = FORWARD, beforeFirstOccurrenceOf = "X").end()
            .addText("after")
            .build()

        val result = ContentPartsPreprocessorValidator.validatePreprocessing(input)

        assertEquals(input, result)
    }
}
