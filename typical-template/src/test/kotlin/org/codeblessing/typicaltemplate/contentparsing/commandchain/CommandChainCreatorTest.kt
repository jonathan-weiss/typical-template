package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TextContentPart
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CommandChainCreatorTest {

    @Nested
    inner class IsListAttributeValidation {

        @Test
        fun `isList defaults to false when attribute is absent`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelName = "myModel")
                    .end()
                    .end()
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            val model = templates.single().modelClasses.single()
            assertEquals(false, model.isList)
        }

        @Test
        fun `isList is true when isList attribute is set to true`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelName = "myModel", isList = true)
                    .end()
                    .end()
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            val model = templates.single().modelClasses.single()
            assertEquals(true, model.isList)
        }

        @Test
        fun `multiple models can independently have isList set`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelName = "singleModel", isList = false)
                        .addTemplateModel(modelName = "listModel", isList = true)
                    .end()
                    .end()
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            val models = templates.single().modelClasses
            assertEquals(false, models[0].isList)
            assertEquals(true, models[1].isList)
        }
    }

    @Nested
    inner class NestedTemplateRendererValidation {

        @Test
        fun `single nested template-renderer produces two descriptions`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                    .end()
                .addText("outer content")
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "InnerRenderer")
                    .end()
                .addText("inner content")
                .addTemplateComment()
                    .addEndTemplateRendererCommand()
                    .end()
                .addText("more outer content")
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(2, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
            assertEquals("InnerRenderer", templates[1].templateRendererClass.className)
        }

        @Test
        fun `top-level end-template-renderer is optional`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                    .end()
                .addText("outer content")
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(1, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
        }

        @Test
        fun `top-level end-template-renderer is accepted when present`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                    .end()
                .addText("outer content")
                .addTemplateComment()
                    .addEndTemplateRendererCommand()
                    .end()
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(1, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
        }

        @Test
        fun `nested template context is isolated from outer`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel(templateRendererClassName = "OuterRenderer")
                        .addTemplateModel(modelName = "outerModel", modelClassName = "OuterModel")
                    .end()
                    .end()
                .addText("outer content")
                .addTemplateComment()
                    .addReplaceValueByExpressionCommand(searchValue = "outerSearch", fieldName = "outerModel.field")
                    .end()
                .addText("text with outerSearch")
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel(templateRendererClassName = "InnerRenderer")
                        .addTemplateModel(modelName = "innerModel", modelClassName = "InnerModel")
                    .end()
                    .end()
                .addText("inner content")
                .addTemplateComment()
                    .addEndTemplateRendererCommand()
                    .end()
                .addText("more outer text with outerSearch")
                .addTemplateComment()
                    .addEndReplaceValueByExpressionCommand()
                    .end()
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
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
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "Level0")
                    .end()
                .addText("level 0 content")
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "Level1")
                    .end()
                .addText("level 1 content")
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "Level2")
                    .end()
                .addText("level 2 content")
                .addTemplateComment()
                    .addEndTemplateRendererCommand()
                    .end()
                .addText("more level 1 content")
                .addTemplateComment()
                    .addEndTemplateRendererCommand()
                    .end()
                .addText("more level 0 content")
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(3, templates.size)
            assertEquals("Level0", templates[0].templateRendererClass.className)
            assertEquals("Level1", templates[1].templateRendererClass.className)
            assertEquals("Level2", templates[2].templateRendererClass.className)
        }

        @Test
        fun `multiple sibling nested template-renderers`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                    .end()
                .addText("outer content")
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "Inner1")
                    .end()
                .addText("inner 1 content")
                .addTemplateComment()
                    .addEndTemplateRendererCommand()
                    .end()
                .addText("between nested")
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "Inner2")
                    .end()
                .addText("inner 2 content")
                .addTemplateComment()
                    .addEndTemplateRendererCommand()
                    .end()
                .addText("more outer content")
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(3, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
            assertEquals("Inner1", templates[1].templateRendererClass.className)
            assertEquals("Inner2", templates[2].templateRendererClass.className)
        }

        @Test
        fun `nested renderer with its own model commands works`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel(templateRendererClassName = "OuterRenderer")
                        .addTemplateModel(modelName = "outerModel", modelClassName = "OuterModel")
                    .end()
                    .end()
                .addText("outer content")
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel(templateRendererClassName = "InnerRenderer")
                        .addTemplateModel(modelName = "innerModel1", modelClassName = "InnerModel1")
                        .addTemplateModel(modelName = "innerModel2", modelClassName = "InnerModel2")
                    .end()
                    .end()
                .addText("inner content")
                .addTemplateComment()
                    .addEndTemplateRendererCommand()
                    .end()
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(2, templates.size)
            assertEquals("InnerRenderer", templates[1].templateRendererClass.className)
            assertEquals(2, templates[1].modelClasses.size)
            assertEquals("innerModel1", templates[1].modelClasses[0].modelName)
            assertEquals("innerModel2", templates[1].modelClasses[1].modelName)
        }
    }

    @Nested
    inner class CommandsAlongsideTemplateRenderer {

        @Test
        fun `replace-value-by-expression in same block as template-renderer is added to chain`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel()
                    .end()
                    .addReplaceValueByExpressionCommand()
                    .end()
                .addText("text with search")
                .addTemplateComment()
                    .addEndReplaceValueByExpressionCommand()
                    .end()
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(1, templates.size)
            val chain = templates.single().templateChain
            assertEquals(3, chain.size)
            assertEquals(CommandKey.REPLACE_VALUE_BY_EXPRESSION, (chain[0] as TemplateCommentContentPart).keywordCommands.single().commandKey)
            assertEquals(true, chain[1] is TextContentPart)
            assertEquals(CommandKey.END_REPLACE_VALUE_BY_EXPRESSION, (chain[2] as TemplateCommentContentPart).keywordCommands.single().commandKey)
        }
    }

    @Nested
    inner class ChainItemTypes {

        @Test
        fun `text part produces TextContentPart in chain`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand()
                    .end()
                .addText("here is text")
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(1, templates.size)
            val template = templates.single()
            assertEquals(1, template.templateChain.size)
            assertEquals(true, template.templateChain.single() is TextContentPart)
        }

        @Test
        fun `template-renderer command is removed from chain`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand()
                    .end()
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(1, templates.size)
            val template = templates.single()
            assertEquals(0, template.templateChain.size)
        }
    }
}
