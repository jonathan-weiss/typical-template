package org.codeblessing.typicaltemplate.contentparsing.resolver

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.commentparser.CommandStructure
import org.codeblessing.typicaltemplate.contentparsing.commentparser.TemplateCommentParser
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ContentPartFactoryTest {

    @Test
    fun `valid text content part is created`() {
        val contentPart = ContentPartFactory.createTextContentPart("my content", stubLineNumbers)
        Assertions.assertEquals("my content", contentPart.text)
    }

    @Test
    fun `valid command content part is created`() {
        val commandStructure = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererClassName="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ]
            """.trimIndent()
        )

        val contentPart = ContentPartFactory.createCommandContentPart(commandStructure, stubLineNumbers)
        val keywordCommand = contentPart.keywordCommand
        Assertions.assertEquals(CommandKey.TEMPLATE_RENDERER, keywordCommand.commandKey)
        Assertions.assertEquals(
            "MyTemplate",
            keywordCommand.attribute(CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME)
        )
        Assertions.assertEquals(
            "org.codeblessing.typicaltemplate.examples",
            keywordCommand.attribute(CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME)
        )
    }

    @Test
    fun `throws for unknown keyword`() {
        val commandStructure = createSingleTemplateComment(
            comment = """ 
                        @unknown
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            ContentPartFactory.createCommandContentPart(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `throws for too few attribute groups`() {
        val commandStructure = createSingleTemplateComment(
            comment = """ 
                        @template-renderer
            """.trimIndent()
        )


        assertThrows<TemplateParsingException> {
            ContentPartFactory.createCommandContentPart(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `throws for too many attribute groups`() {
        val commandStructure = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererClassName="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ][
                            templateRendererClassName="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            ContentPartFactory.createCommandContentPart(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `throws for unknown attribute key`() {
        val commandStructure = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererClassNameUnknown="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            ContentPartFactory.createCommandContentPart(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `throws for unallowed attribute key`() {
        val commandStructure = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererClassName="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                            replacement="foo"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            ContentPartFactory.createCommandContentPart(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `throws for unallowed attribute value`() {
        val commandStructure = createSingleTemplateComment(
            comment = """ 
                        @template-model [
                            modelName="myTemplateModel"
                            modelClassName="MyTemplateModel"
                            modelPackageName="org.codeblessing.typicaltemplate.examples"
                            isList="foo"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            ContentPartFactory.createCommandContentPart(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `throws for blank attribute value when not allowed`() {
        val commandStructure = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererClassName=""
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            ContentPartFactory.createCommandContentPart(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `throws for missing required attribute`() {
        val commandStructure = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            ContentPartFactory.createCommandContentPart(commandStructure, stubLineNumbers)
        }
    }

    private val stubLineNumbers = LineNumbers.EMPTY_LINE_NUMBERS

    private fun createSingleTemplateComment(comment: String): CommandStructure {
        return TemplateCommentParser.parseComment(comment).single()
    }
}
