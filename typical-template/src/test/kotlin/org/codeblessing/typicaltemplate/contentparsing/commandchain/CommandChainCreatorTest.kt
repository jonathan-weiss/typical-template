package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CommandChainCreatorTest {

    @Nested
    inner class IsListAttributeValidation {

        @Test
        fun `isList defaults to false when attribute is absent`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateRendererCommand()
                .addTemplateModel(modelName = "myModel")
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            val model = templates.single().modelClasses.single()
            assertEquals(false, model.isList)
        }

        @Test
        fun `isList is true when isList attribute is set to true`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateRendererCommand()
                .addTemplateModel(modelName = "myModel", isList = true)
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            val model = templates.single().modelClasses.single()
            assertEquals(true, model.isList)
        }

        @Test
        fun `multiple models can independently have isList set`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateRendererCommand()
                .addTemplateModel(modelName = "singleModel", isList = false)
                .addTemplateModel(modelName = "listModel", isList = true)
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
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addText("outer content")
                .addTemplateRendererCommand(templateRendererClassName = "InnerRenderer")
                .addText("inner content")
                .addEndTemplateRendererCommand()
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
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addText("outer content")
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(1, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
        }

        @Test
        fun `top-level end-template-renderer is accepted when present`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addText("outer content")
                .addEndTemplateRendererCommand()
                .build()

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(1, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
        }

        @Test
        fun `nested template context is isolated from outer`() {
            val contentParts = ContentPartBuilder.create()
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

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(3, templates.size)
            assertEquals("Level0", templates[0].templateRendererClass.className)
            assertEquals("Level1", templates[1].templateRendererClass.className)
            assertEquals("Level2", templates[2].templateRendererClass.className)
        }

        @Test
        fun `multiple sibling nested template-renderers`() {
            val contentParts = ContentPartBuilder.create()
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

            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(3, templates.size)
            assertEquals("OuterRenderer", templates[0].templateRendererClass.className)
            assertEquals("Inner1", templates[1].templateRendererClass.className)
            assertEquals("Inner2", templates[2].templateRendererClass.className)
        }

        @Test
        fun `nested renderer with its own model commands works`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateRendererCommand(templateRendererClassName = "OuterRenderer")
                .addTemplateModel(modelName = "outerModel", modelClassName = "OuterModel")
                .addText("outer content")
                .addTemplateRendererCommand(templateRendererClassName = "InnerRenderer")
                .addTemplateModel(modelName = "innerModel1", modelClassName = "InnerModel1")
                .addTemplateModel(modelName = "innerModel2", modelClassName = "InnerModel2")
                .addText("inner content")
                .addEndTemplateRendererCommand()
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
    inner class MutuallyInfluencedChainItems {

        @Test
        fun `do not mark plain text item if no influencing commands are in the chain`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateRendererCommand()
                .addText("here is text")
                .build()


            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(1, templates.size)
            val template = templates.single()
            assertEquals(1, template.templateChain.size)
            val plainTextChainItem = template.templateChain.single() as PlainTextChainItem

            assertEquals(false, plainTextChainItem.removeFirstLineIfWhitespaces)
            assertEquals(false, plainTextChainItem.removeLastLineIfWhitespaces)
        }

        @Test
        fun `mark previous plain text item to remove last line on directly following strip-line-before command`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateRendererCommand()
                .addText("here is text")
                .addStripLineBeforeCommentCommand()
                .build()


            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(1, templates.size)
            val template = templates.single()
            assertEquals(1, template.templateChain.size)
            val plainTextChainItem = template.templateChain.single() as PlainTextChainItem

            assertEquals(false, plainTextChainItem.removeFirstLineIfWhitespaces)
            assertEquals(true, plainTextChainItem.removeLastLineIfWhitespaces)
        }

        @Test
        fun `mark next plain text item to remove first line on directly preceding strip-line-after command`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateRendererCommand()
                .addStripLineAfterCommentCommand()
                .addText("here is text")
                .build()


            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(1, templates.size)
            val template = templates.single()
            assertEquals(1, template.templateChain.size)
            val plainTextChainItem = template.templateChain.single() as PlainTextChainItem

            assertEquals(true, plainTextChainItem.removeFirstLineIfWhitespaces)
            assertEquals(false, plainTextChainItem.removeLastLineIfWhitespaces)
        }

        @Test
        fun `remove commands from chain without effects on text if no text is available`() {
            val contentParts = ContentPartBuilder.create()
                .addTemplateRendererCommand()
                .addStripLineBeforeCommentCommand()
                .addStripLineAfterCommentCommand()
                .build()


            val templates = CommandChainCreator.validateAndInterpretContentParts(contentParts)
            assertEquals(1, templates.size)
            val template = templates.single()
            assertEquals(0, template.templateChain.size)
        }
    }
}
