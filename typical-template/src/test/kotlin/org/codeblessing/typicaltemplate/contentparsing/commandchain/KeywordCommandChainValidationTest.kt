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
                .addTemplateComment().addTemplateRendererCommand().end()
                .addText("here is text")
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addTemplateComment().addEndReplaceValueByExpressionCommand().end()
                .build()

            KeywordCommandChainValidation.validate(contentParts)
        }

        @Test
        fun `valid template chain with nested commands is accepted`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateComment().addTemplateRendererCommand().end()
                .addText("here is text")
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addTemplateComment().addIfCommand("myConditionExpression").end()
                .addText("inside if statement")
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addText("replace expression inside if statement")
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addText("replace expression inside replace expression statement")
                .addTemplateComment().addEndReplaceValueByExpressionCommand().end()
                .addTemplateComment().addEndReplaceValueByExpressionCommand().end()
                .addTemplateComment().addElseCommand().end()
                .addText("inside else statement")
                .addTemplateComment().addEndIfCommand().end()
                .addTemplateComment().addEndReplaceValueByExpressionCommand().end()
                .build()

            KeywordCommandChainValidation.validate(contentParts)
        }

        @Test
        fun `valid template chain with nested commands that are autoclosed is accepted`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateComment().addTemplateRendererCommand().end()
                .addText("here is text")
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addTemplateComment().addIfCommand("myConditionExpression").end()
                .addText("inside if statement")
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addText("replace expression inside if statement")
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addText("replace expression inside replace expression statement")
                // replace-value-by-expression command is autoclosed by else command
                // replace-value-by-expression command is autoclosed by else command
                .addTemplateComment().addElseCommand().end()
                .addText("inside else statement")
                .addTemplateComment().addEndIfCommand().end()
                // replace-value-by-expression command is autoclosed by end of command chain
                .build()

            KeywordCommandChainValidation.validate(contentParts)
        }

        @Test
        fun `throws for unmatched closing command`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateComment().addTemplateRendererCommand().end()
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addTemplateComment().addEndIfCommand().end()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `valid template chain for unclosed opening command that supports auto-closing`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateComment().addTemplateRendererCommand().end()
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .build()

            KeywordCommandChainValidation.validate(contentParts)
        }

        @Test
        fun `throws for unclosed opening command that do not supports auto-closing`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateComment().addTemplateRendererCommand().end()
                .addTemplateComment().addIfCommand().end()
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
                .addTemplateComment().addTemplateRendererCommand().end()
                .addTemplateComment().addIfCommand("model.isSerializable()").end()
                .addText("only if serializable")
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addTemplateComment().addEndIfCommand().end() // replace is inside of if and must be closed first
                .addTemplateComment().addEndReplaceValueByExpressionCommand().end()
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
                .addTemplateComment().addTemplateRendererCommand().end()
                .addTemplateComment().addTemplateRendererCommand().end()
                .addTemplateComment().addTemplateModel().end()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `throws for multiple model commands with same model name`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateComment().addTemplateRendererCommand().end()
                .addTemplateComment().addTemplateModel(modelName = "myModel").end()
                .addTemplateComment().addTemplateModel(modelName = "myModel").end()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `throws if first command is not template definition`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addTemplateComment().addEndReplaceValueByExpressionCommand().end()
                .addTemplateComment().addTemplateRendererCommand().end()
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
                .addTemplateComment().addTemplateRendererCommand().end()
                .addTemplateComment().addIfCommand("model.isSerializable()").end()
                .addText("here is text where mySearchValue is replaced by the placeholder myFieldName")
                .addTemplateComment().addEndIfCommand().end()
                .addTemplateComment().addElseCommand().end()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `throws for else if command not in if statement`() {
            val contentParts = ContentPartBuilder.create()
                .addText("here is text")
                .addTemplateComment().addTemplateRendererCommand().end()
                .addTemplateComment().addIfCommand("model.isSerializable()").end()
                .addText("only if serializable")
                .addTemplateComment().addEndIfCommand().end()
                .addTemplateComment().addElseIfCommand("model.isEnum()").end()
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
                .addTemplateComment().addTemplateRendererCommand(templateRendererClassName = "OuterRenderer").end()
                .addText("outer content")
                .addTemplateComment().addTemplateRendererCommand(templateRendererClassName = "InnerRenderer").end()
                .addText("inner content")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `end-template-renderer without opener throws`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment().addEndTemplateRendererCommand().end()
                .addText("some content")
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }

        @Test
        fun `commands after top-level end-template-renderer throws`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment().addTemplateRendererCommand(templateRendererClassName = "OuterRenderer").end()
                .addText("outer content")
                .addTemplateComment().addEndTemplateRendererCommand().end()
                .addTemplateComment().addReplaceValueByExpressionCommand().end()
                .addText("this should not be allowed")
                .addTemplateComment().addEndReplaceValueByExpressionCommand().end()
                .build()

            Assertions.assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainValidation.validate(contentParts)
            }
        }
    }
}
