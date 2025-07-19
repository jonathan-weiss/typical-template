package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.CommandChainBuilder
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CommandChainCreatorTest {
    @Test
    fun `valid template chain is accepted`() {
        val fragments = CommandChainBuilder.Companion.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addText("here is text")
            .addReplaceValueByExpressionCommand()
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .addEndReplaceValueByExpressionCommand()
            .build()

        val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
        Assertions.assertEquals(1, templates.size)

    }

    @Test
    fun `throws for no template definition command`() {
        val fragments = CommandChainBuilder.Companion.create()
            .addText("here is text")
            .build()

        Assertions.assertThrows(TemplateParsingException::class.java) {
            CommandChainCreator.validateAndInterpretFragments(fragments)
        }
    }

    @Test
    fun `throws for multiple template definition commands`() {
        val fragments = CommandChainBuilder.Companion.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addTemplateRendererCommand()
            .addTemplateModel()
            .build()

        Assertions.assertThrows(TemplateParsingException::class.java) {
            CommandChainCreator.validateAndInterpretFragments(fragments)
        }
    }

    @Test
    fun `throws for multiple model commands with same model name`() {
        val fragments = CommandChainBuilder.Companion.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addTemplateModel(modelName = "myModel")
            .addTemplateModel(modelName = "myModel")
            .build()

        Assertions.assertThrows(TemplateParsingException::class.java) {
            CommandChainCreator.validateAndInterpretFragments(fragments)
        }
    }

    @Test
    fun `throws if first command is not template definition`() {
        val fragments = CommandChainBuilder.Companion.create()
            .addText("here is text")
            .addReplaceValueByExpressionCommand()
            .addEndReplaceValueByExpressionCommand()
            .addTemplateRendererCommand()
            .build()

        Assertions.assertThrows(TemplateParsingException::class.java) {
            CommandChainCreator.validateAndInterpretFragments(fragments)
        }
    }

    @Test
    fun `throws for unmatched closing command`() {
        val fragments = CommandChainBuilder.Companion.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addReplaceValueByExpressionCommand()
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .addEndIfCommand()
            .build()

        Assertions.assertThrows(TemplateParsingException::class.java) {
            CommandChainCreator.validateAndInterpretFragments(fragments)
        }
    }

    @Test
    fun `throws for unclosed opening command`() {
        val fragments = CommandChainBuilder.Companion.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addReplaceValueByExpressionCommand()
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .build()

        Assertions.assertThrows(TemplateParsingException::class.java) {
            CommandChainCreator.validateAndInterpretFragments(fragments)
        }
    }

    @Test
    fun `throws for else command not in if statement`() {
        val fragments = CommandChainBuilder.Companion.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addIfCommand("model.isSerializable()")
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .addEndIfCommand()
            .addElseCommand()
            .build()

        Assertions.assertThrows(TemplateParsingException::class.java) {
            CommandChainCreator.validateAndInterpretFragments(fragments)
        }
    }

    @Test
    fun `throws for else if command not in if statement`() {
        val fragments = CommandChainBuilder.Companion.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addIfCommand("model.isSerializable()")
            .addText("only if serializable")
            .addEndIfCommand()
            .addElseIfCommand("model.isEnum()")
            .build()

        Assertions.assertThrows(TemplateParsingException::class.java) {
            CommandChainCreator.validateAndInterpretFragments(fragments)
        }
    }

    @Test
    fun `throws for invalid open and closing command mix`() {
        val fragments = CommandChainBuilder.Companion.create()
            .addText("here is text")
            .addTemplateRendererCommand()
            .addIfCommand("model.isSerializable()")
            .addText("only if serializable")
            .addReplaceValueByExpressionCommand()
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .addEndIfCommand() // replace is inside of if and must be closed first
            .addEndReplaceValueByExpressionCommand()
            .build()

        Assertions.assertThrows(TemplateParsingException::class.java) {
            CommandChainCreator.validateAndInterpretFragments(fragments)
        }
    }
}
