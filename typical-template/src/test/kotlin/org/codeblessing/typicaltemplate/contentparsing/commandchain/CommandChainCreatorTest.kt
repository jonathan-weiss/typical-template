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
    inner class NestedTemplateRendererValidation {

        @Test
        fun `single nested template-renderer produces two descriptions`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addText("outer content")
                .addTemplateRendererCommand(templateRendererClassName = "InnerRenderer")
                .addText("inner content")
                .addEndTemplateRendererCommand()
                .addText("more outer content")
                .build()

            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(2, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
            assertEquals("InnerRenderer", templates[1].templateRendererClass.className)
        }

        @Test
        fun `nested template-renderer without end-template-renderer throws`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addText("outer content")
                .addTemplateRendererCommand(templateRendererClassName = "InnerRenderer")
                .addText("inner content")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                CommandChainCreator.validateAndInterpretFragments(fragments)
            }
        }

        @Test
        fun `top-level end-template-renderer is optional`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addText("outer content")
                .build()

            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(1, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
        }

        @Test
        fun `top-level end-template-renderer is accepted when present`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addText("outer content")
                .addEndTemplateRendererCommand()
                .build()

            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(1, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
        }

        @Test
        fun `nested template context is isolated from outer`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addTemplateModel(modelName = "outerModel", modelClassName = "OuterModel")
                .addText("outer content")
                .addReplaceValueByExpressionCommand(searchValue = "outerSearch", fieldName = "outerModel.field")
                .addText("text with outerSearch")
                .addTemplateRendererCommand(templateRendererClassName = "InnerRenderer")
                .addTemplateModel(modelName = "innerModel", modelClassName = "InnerModel")
                .addText("inner content")
                .addEndTemplateRendererCommand()
                .addText("more outer text with outerSearch")
                .addEndReplaceValueByExpressionCommand()
                .build()

            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(2, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
            assertEquals(1, templates[0].modelClasses.size)
            assertEquals("outerModel", templates[0].modelClasses[0].modelName)
            assertEquals("InnerRenderer", templates[1].templateRendererClass.className)
            assertEquals(1, templates[1].modelClasses.size)
            assertEquals("innerModel", templates[1].modelClasses[0].modelName)
        }

        @Test
        fun `deeply nested template-renderers supported`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "Level0")
                .addText("level 0 content")
                .addTemplateRendererCommand(templateRendererClassName = "Level1")
                .addText("level 1 content")
                .addTemplateRendererCommand(templateRendererClassName = "Level2")
                .addText("level 2 content")
                .addEndTemplateRendererCommand()
                .addText("more level 1 content")
                .addEndTemplateRendererCommand()
                .addText("more level 0 content")
                .build()

            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(3, templates.size)
            assertEquals("Level0", templates[0].templateRendererClass.className)
            assertEquals("Level1", templates[1].templateRendererClass.className)
            assertEquals("Level2", templates[2].templateRendererClass.className)
        }

        @Test
        fun `multiple sibling nested template-renderers`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addText("outer content")
                .addTemplateRendererCommand(templateRendererClassName = "Inner1")
                .addText("inner 1 content")
                .addEndTemplateRendererCommand()
                .addText("between nested")
                .addTemplateRendererCommand(templateRendererClassName = "Inner2")
                .addText("inner 2 content")
                .addEndTemplateRendererCommand()
                .addText("more outer content")
                .build()

            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(3, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
            assertEquals("Inner1", templates[1].templateRendererClass.className)
            assertEquals("Inner2", templates[2].templateRendererClass.className)
        }

        @Test
        fun `end-template-renderer without opener throws`() {
            val fragments = FragmentsBuilder.create()
                .addEndTemplateRendererCommand()
                .addText("some content")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                CommandChainCreator.validateAndInterpretFragments(fragments)
            }
        }

        @Test
        fun `commands after top-level end-template-renderer throws`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addText("outer content")
                .addEndTemplateRendererCommand()
                .addReplaceValueByExpressionCommand()
                .addText("this should not be allowed")
                .addEndReplaceValueByExpressionCommand()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                CommandChainCreator.validateAndInterpretFragments(fragments)
            }
        }

        @Test
        fun `nested renderer with its own model commands works`() {
            val fragments = FragmentsBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addTemplateModel(modelName = "outerModel", modelClassName = "OuterModel")
                .addText("outer content")
                .addTemplateRendererCommand(templateRendererClassName = "InnerRenderer")
                .addTemplateModel(modelName = "innerModel1", modelClassName = "InnerModel1")
                .addTemplateModel(modelName = "innerModel2", modelClassName = "InnerModel2")
                .addText("inner content")
                .addEndTemplateRendererCommand()
                .build()

            val templates = CommandChainCreator.validateAndInterpretFragments(fragments)
            assertEquals(2, templates.size)
            assertEquals("InnerRenderer", templates[1].templateRendererClass.className)
            assertEquals(2, templates[1].modelClasses.size)
            assertEquals("innerModel1", templates[1].modelClasses[0].modelName)
            assertEquals("innerModel2", templates[1].modelClasses[1].modelName)
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
