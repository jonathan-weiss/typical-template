package org.codeblessing.tavnit.contentparsing.commandchain

import org.codeblessing.tavnit.CommandAttributeKey
import org.codeblessing.tavnit.CommandKey
import org.codeblessing.tavnit.contentparsing.TemplateParsingErrorCode
import org.codeblessing.tavnit.contentparsing.TemplateParsingException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KeywordCommandChainCustomValidationTest {

    @Nested
    inner class PassThrough {

        @Test
        fun `empty list returns empty list`() {
            val input = ContentPartBuilder.create().build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }

        @Test
        fun `text-only parts are passed through unchanged`() {
            val input = ContentPartBuilder.create()
                .addText("some text")
                .build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }

        @Test
        fun `command without validated attribute keys passes through unchanged`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addIfCommand()
                    .end()
                .build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }
    }

    @Nested
    inner class RendererClassName {

        @Test
        fun `valid renderer class name passes validation`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "MyRenderer")
                    .end()
                .build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }

        @Test
        fun `renderer class name starting with digit throws exception`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "123Renderer")
                    .end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainCustomValidation.validate(input)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_JAVA_CLASS_NAME, exception.errorCode)
        }

        @Test
        fun `java keyword as renderer class name throws exception`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererClassName = "class")
                    .end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainCustomValidation.validate(input)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_JAVA_CLASS_NAME, exception.errorCode)
        }
    }

    @Nested
    inner class RendererInterfaceName {

        @Test
        fun `valid renderer interface name passes validation`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .createCommand(CommandKey.TEMPLATE_RENDERER)
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME, "MyRenderer")
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME, "org.example")
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_INTERFACE_NAME, "MyInterface")
                        .addCommandToChain()
                    .end()
                .build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }

        @Test
        fun `invalid renderer interface name throws exception`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .createCommand(CommandKey.TEMPLATE_RENDERER)
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME, "MyRenderer")
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME, "org.example")
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_INTERFACE_NAME, "123Interface")
                        .addCommandToChain()
                    .end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainCustomValidation.validate(input)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_JAVA_CLASS_NAME, exception.errorCode)
        }
    }

    @Nested
    inner class RendererPackageName {

        @Test
        fun `valid renderer package name passes validation`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererPackageName = "org.example.template")
                    .end()
                .build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }

        @Test
        fun `invalid renderer package name throws exception`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommand(templateRendererPackageName = "org.123.template")
                    .end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainCustomValidation.validate(input)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_JAVA_PACKAGE_NAME, exception.errorCode)
        }
    }

    @Nested
    inner class RendererInterfacePackageName {

        @Test
        fun `valid renderer interface package name passes validation`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .createCommand(CommandKey.TEMPLATE_RENDERER)
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME, "MyRenderer")
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME, "org.example")
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_INTERFACE_PACKAGE_NAME, "org.example.interfaces")
                        .addCommandToChain()
                    .end()
                .build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }

        @Test
        fun `invalid renderer interface package name throws exception`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .createCommand(CommandKey.TEMPLATE_RENDERER)
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME, "MyRenderer")
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME, "org.example")
                        .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_INTERFACE_PACKAGE_NAME, "org.123.interfaces")
                        .addCommandToChain()
                    .end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainCustomValidation.validate(input)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_JAVA_PACKAGE_NAME, exception.errorCode)
        }
    }

    @Nested
    inner class ModelClassName {

        @Test
        fun `valid model class name passes validation`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelClassName = "MyModel")
                        .end()
                    .end()
                .build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }

        @Test
        fun `invalid model class name throws exception`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelClassName = "123Model")
                        .end()
                    .end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainCustomValidation.validate(input)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_JAVA_CLASS_NAME, exception.errorCode)
        }
    }

    @Nested
    inner class ModelPackageName {

        @Test
        fun `valid model package name passes validation`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelPackageName = "org.example.model")
                        .end()
                    .end()
                .build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }

        @Test
        fun `invalid model package name throws exception`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelPackageName = "org.123.model")
                        .end()
                    .end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainCustomValidation.validate(input)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_JAVA_PACKAGE_NAME, exception.errorCode)
        }
    }

    @Nested
    inner class ModelName {

        @Test
        fun `valid model name passes validation`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelName = "myModel")
                        .end()
                    .end()
                .build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }

        @Test
        fun `model name starting with digit throws exception`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelName = "123model")
                        .end()
                    .end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainCustomValidation.validate(input)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_JAVA_PARAMETER_NAME, exception.errorCode)
        }

        @Test
        fun `java keyword as model name throws exception`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelName = "for")
                        .end()
                    .end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainCustomValidation.validate(input)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_JAVA_PARAMETER_NAME, exception.errorCode)
        }
    }

    @Nested
    inner class UniqueModelNames {

        @Test
        fun `two models with different names in same command passes validation`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelName = "model1")
                        .addTemplateModel(modelName = "model2")
                        .end()
                    .end()
                .build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }

        @Test
        fun `two models with same name in same command throws exception`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelName = "model")
                        .addTemplateModel(modelName = "model")
                        .end()
                    .end()
                .build()

            val exception = assertThrows(TemplateParsingException::class.java) {
                KeywordCommandChainCustomValidation.validate(input)
            }
            assertEquals(TemplateParsingErrorCode.DUPLICATE_MODEL_NAME, exception.errorCode)
        }

        @Test
        fun `same model name in different commands passes validation`() {
            val input = ContentPartBuilder.create()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelName = "model")
                        .end()
                    .end()
                .addTemplateComment()
                    .addTemplateRendererCommandWithTemplateModel()
                        .addTemplateModel(modelName = "model")
                        .end()
                    .end()
                .build()

            val result = KeywordCommandChainCustomValidation.validate(input)

            assertEquals(input, result)
        }
    }
}
