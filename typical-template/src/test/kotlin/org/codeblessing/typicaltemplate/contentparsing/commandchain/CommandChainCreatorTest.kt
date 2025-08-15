package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CommandChainCreatorTest {
    
    
    @Nested
    inner class GeneralValidation {

        @Test
        fun `valid template chain is accepted`() {
            val fragments = FragmentsBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addText("here is text")
                .addReplaceValueByExpressionCommand()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addEndReplaceValueByExpressionCommand()
                .build()

            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(1, templates.size)

        }

        @Test
        fun `valid template chain with nested commands is accepted`() {
            val fragments = FragmentsBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addText("here is text")
                .addReplaceValueByExpressionCommand()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addIfCommand("myConditionExpression")
                .addText("inside if statement")
                .addReplaceValueByExpressionCommand()
                .addText("replace expression inside if statement")
                .addReplaceValueByExpressionCommand()
                .addText("replace expression inside replace expression statement")
                .addEndReplaceValueByExpressionCommand()
                .addEndReplaceValueByExpressionCommand()
                .addElseCommand()
                .addText("inside else statement")
                .addEndIfCommand()
                .addEndReplaceValueByExpressionCommand()
                .build()

            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(1, templates.size)
        }

        @Test
        fun `valid template chain with nested commands that are autoclosed is accepted`() {
            val fragments = FragmentsBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addText("here is text")
                .addReplaceValueByExpressionCommand()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addIfCommand("myConditionExpression")
                .addText("inside if statement")
                .addReplaceValueByExpressionCommand()
                .addText("replace expression inside if statement")
                .addReplaceValueByExpressionCommand()
                .addText("replace expression inside replace expression statement")
                // replace-value-by-expression command is autoclosed by else command
                // replace-value-by-expression command is autoclosed by else command
                .addElseCommand()
                .addText("inside else statement")
                .addEndIfCommand()
                // replace-value-by-expression command is autoclosed by end of command chain
                .build()

            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(1, templates.size)
        }

        @Test
        fun `throws for unmatched closing command`() {
            val fragments = FragmentsBuilder.create()
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
        fun `valid template chain for unclosed opening command that supports auto-closing`() {
            val fragments = FragmentsBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addReplaceValueByExpressionCommand()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .build()

            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(1, templates.size)
        }

        @Test
        fun `throws for unclosed opening command that do not supports auto-closing`() {
            val fragments = FragmentsBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addIfCommand()
                .addText("here is the content of the if command")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                CommandChainCreator.validateAndInterpretFragments(fragments)
            }
        }

        @Test
        fun `throws for invalid open and closing command mix`() {
            val fragments = FragmentsBuilder.create()
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

    @Nested
    inner class TemplateRendererAndModelValidation {

        @Test
        fun `throws for no template definition command`() {
            val fragments = FragmentsBuilder.create()
                .addText("here is text")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                CommandChainCreator.validateAndInterpretFragments(fragments)
            }
        }

        @Test
        fun `throws for multiple template definition commands`() {
            val fragments = FragmentsBuilder.create()
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
            val fragments = FragmentsBuilder.create()
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
            val fragments = FragmentsBuilder.create()
                .addText("here is text")
                .addReplaceValueByExpressionCommand()
                .addEndReplaceValueByExpressionCommand()
                .addTemplateRendererCommand()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                CommandChainCreator.validateAndInterpretFragments(fragments)
            }
        }
    }

    @Nested
    inner class NestedCommandsValidation {

        @Test
        fun `throws for else command not in if statement`() {
            val fragments = FragmentsBuilder.create()
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
            val fragments = FragmentsBuilder.create()
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
    }


    @Nested
    inner class MutuallyInfluencedChainItems {

        @Test
        fun `do not mark plain text item if no influencing commands are in the chain`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand()
                .addText("here is text")
                .build()


            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(1, templates.size)
            val template = templates.single()
            assertEquals(1, template.templateChain.size)
            val plainTextChainItem = template.templateChain.single() as PlainTextChainItem

            assertEquals(false, plainTextChainItem.removeFirstLineIfWhitespaces)
            assertEquals(false, plainTextChainItem.removeLastLineIfWhitespaces)
        }
        @Test
        fun `mark previous plain text item to remove last line on directly following strip-line-before command`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand()
                .addText("here is text")
                .addStripLineBeforeCommentCommand()
                .build()


            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(1, templates.size)
            val template = templates.single()
            assertEquals(1, template.templateChain.size)
            val plainTextChainItem = template.templateChain.single() as PlainTextChainItem

            assertEquals(false, plainTextChainItem.removeFirstLineIfWhitespaces)
            assertEquals(true, plainTextChainItem.removeLastLineIfWhitespaces)
        }

        @Test
        fun `mark next plain text item to remove first line on directly preceding strip-line-after command`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand()
                .addStripLineAfterCommentCommand()
                .addText("here is text")
                .build()


            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(1, templates.size)
            val template = templates.single()
            assertEquals(1, template.templateChain.size)
            val plainTextChainItem = template.templateChain.single() as PlainTextChainItem

            assertEquals(true, plainTextChainItem.removeFirstLineIfWhitespaces)
            assertEquals(false, plainTextChainItem.removeLastLineIfWhitespaces)
        }

        @Test
        fun `remove commands from chain without effects on text if no text is available`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand()
                .addStripLineBeforeCommentCommand()
                .addStripLineAfterCommentCommand()
                .build()


            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(1, templates.size)
            val template = templates.single()
            assertEquals(0, template.templateChain.size)
        }

    }
}
