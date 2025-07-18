package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.CommandChainBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class CommandChainValidatorTest {
    @Test
    fun `valid template chain is accepted`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addText("here is text")
            .addReplaceValueByExpressionCommand()
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .addEndReplaceValueByExpressionCommand()
            .build()

        val templates = CommandChainValidator.validateCommands(fragments)
        assertEquals(1, templates.size)

    }

    @Test
    fun `throws for no template definition command`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }

    @Test
    fun `throws for multiple template definition commands`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addTemplateRendererCommand()
            .addTemplateModel()
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }

    @Test
    fun `throws for multiple model commands with same model name`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addTemplateModel(modelName = "myModel")
            .addTemplateModel(modelName = "myModel")
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }

    @Test
    fun `throws if first command is not template definition`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .addReplaceValueByExpressionCommand()
            .addEndReplaceValueByExpressionCommand()
            .addTemplateRendererCommand()
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }

    @Test
    fun `throws for unmatched closing command`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addReplaceValueByExpressionCommand()
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .addEndIfCommand()
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }

    @Test
    fun `throws for unclosed opening command`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addReplaceValueByExpressionCommand()
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }

    @Test
    fun `throws for else command not in if statement`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addIfCommand("model.isSerializable()")
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .addEndIfCommand()
            .addElseCommand()
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }

    @Test
    fun `throws for else if command not in if statement`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addIfCommand("model.isSerializable()")
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .addEndIfCommand()
            .addElseIfCommand("model.isEnum()")
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }

}
