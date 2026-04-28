package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KeywordCommandChainValidationTest {

    @Nested
    inner class GeneralValidation {

        @Test
        fun `valid template chain is accepted`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addText("here is text")
                .addReplaceValueByExpressionCommand()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addEndReplaceValueByExpressionCommand()
                .build()

            KeywordCommandChainValidation.validate(contentParts)
        }

        @Test
        fun `valid template chain with nested commands is accepted`() {
            val contentParts = ContentPartBuilder.create()
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

            KeywordCommandChainValidation.validate(contentParts)
        }

        @Test
        fun `valid template chain with nested commands that are autoclosed is accepted`() {
            val contentParts = ContentPartBuilder.create()
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

            KeywordCommandChainValidation.validate(contentParts)
        }

        @Test
        fun `throws for unmatched closing command`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addReplaceValueByExpressionCommand()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addEndIfCommand()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `valid template chain for unclosed opening command that supports auto-closing`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addReplaceValueByExpressionCommand()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .build()

            KeywordCommandChainValidation.validate(contentParts)
        }

        @Test
        fun `throws for unclosed opening command that do not supports auto-closing`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addIfCommand()
                .addText("here is the content of the if command")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `throws for invalid open and closing command mix`() {
            val contentParts = ContentPartBuilder.create()
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
                KeywordCommandChainValidation.validate(contentParts)
            }
        }
    }

    @Nested
    inner class TemplateRendererAndModelValidation {

        @Test
        fun `throws for no template definition command`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `throws for multiple template definition commands`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addTemplateRendererCommand()
                .addTemplateModel()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `throws for multiple model commands with same model name`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addTemplateModel(modelName = "myModel")
                .addTemplateModel(modelName = "myModel")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `throws if first command is not template definition`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addReplaceValueByExpressionCommand()
                .addEndReplaceValueByExpressionCommand()
                .addTemplateRendererCommand()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }
    }

    @Nested
    inner class NestedCommandsValidation {

        @Test
        fun `throws for else command not in if statement`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addIfCommand("model.isSerializable()")
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addEndIfCommand()
                .addElseCommand()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `throws for else if command not in if statement`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateRendererCommand()
                .addIfCommand("model.isSerializable()")
                .addText("only if serializable")
                .addEndIfCommand()
                .addElseIfCommand("model.isEnum()")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }
    }

    @Nested
    inner class NestedTemplateRendererValidation {

        @Test
        fun `nested template-renderer without end-template-renderer throws`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addText("outer content")
                .addTemplateRendererCommand(templateRendererClassName = "InnerRenderer")
                .addText("inner content")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `end-template-renderer without opener throws`() {
            val contentParts = ContentPartBuilder.create()
                .addEndTemplateRendererCommand()
                .addText("some content")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `commands after top-level end-template-renderer throws`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addText("outer content")
                .addEndTemplateRendererCommand()
                .addReplaceValueByExpressionCommand()
                .addText("this should not be allowed")
                .addEndReplaceValueByExpressionCommand()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }
    }
}
