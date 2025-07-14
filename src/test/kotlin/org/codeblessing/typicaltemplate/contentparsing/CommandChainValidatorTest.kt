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
            .addTemplateCommand()
            .addText("here is text")
            .addReplaceValueByFieldCommand()
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .addEndReplaceValueByFieldCommand()
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
            .addTemplateCommand()
            .addTemplateCommand()
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }

    @Test
    fun `throws if first command is not template definition`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .addReplaceValueByFieldCommand()
            .addEndReplaceValueByFieldCommand()
            .addTemplateCommand()
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }

    @Test
    @Disabled
    fun `throws for unmatched closing command`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .addTemplateCommand()
            .addReplaceValueByFieldCommand()
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .addEndReplaceValueByFieldCommand()
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }

    @Test
    fun `throws for unclosed opening command`() {
        val fragments = CommandChainBuilder.create()
            .addText("here is text")
            .addTemplateCommand()
            .addReplaceValueByFieldCommand()
            .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
            .build()

        assertThrows(TemplateParsingException::class.java) {
            CommandChainValidator.validateCommands(fragments)
        }
    }
}
