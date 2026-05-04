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

class KeywordCommandFactoryTest {

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

        val keywordCommand = KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
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
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
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
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `valid template-renderer with renderer and model attribute groups`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                        @template-renderer [
                            templateRendererClassName="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ][
                            modelName="myModel"
                            modelClassName="MyModelClass"
                        ]
            """.trimIndent()
        )

        val keywordCommand = KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        Assertions.assertEquals(CommandKey.TEMPLATE_RENDERER, keywordCommand.commandKey)
        Assertions.assertEquals(2, keywordCommand.attributeGroups.size)
        Assertions.assertEquals("MyTemplate", keywordCommand.attribute(0, CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME))
        Assertions.assertEquals("myModel", keywordCommand.attribute(1, CommandAttributeKey.TEMPLATE_MODEL_NAME))
    }

    @Test
    fun `throws when model attribute group contains renderer attributes`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                        @template-renderer [
                            templateRendererClassName="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ][
                            templateRendererClassName="MyTemplate"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
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
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
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
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `throws for unallowed attribute value`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                        @template-renderer [
                            templateRendererClassName="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ][
                            modelName="myTemplateModel"
                            modelClassName="MyTemplateModel"
                            modelPackageName="org.codeblessing.typicaltemplate.examples"
                            isList="foo"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
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
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
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
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `valid command with no attribute constraints`() {
        val commandStructure = createSingleTemplateComment(
            comment = "@end-template-renderer"
        )

        val keywordCommand = KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        Assertions.assertEquals(CommandKey.END_TEMPLATE_RENDERER, keywordCommand.commandKey)
        Assertions.assertTrue(keywordCommand.attributeGroups.isEmpty())
    }

    @Test
    fun `valid MANY attribute group command with single group`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                @replace-value-by-expression [
                    searchValue="old"
                    replaceByExpression="new"
                ]
            """.trimIndent()
        )

        val keywordCommand = KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        Assertions.assertEquals(CommandKey.REPLACE_VALUE_BY_EXPRESSION, keywordCommand.commandKey)
        Assertions.assertEquals("old", keywordCommand.attribute(CommandAttributeKey.SEARCH_VALUE))
        Assertions.assertEquals("new", keywordCommand.attribute(CommandAttributeKey.REPLACE_BY_EXPRESSION))
    }

    @Test
    fun `valid MANY attribute group command with multiple groups`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                @replace-value-by-expression [
                    searchValue="old"
                    replaceByExpression="new"
                ][
                    searchValue="old2"
                    replaceByExpression="new2"
                ]
            """.trimIndent()
        )

        val keywordCommand = KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        Assertions.assertEquals(CommandKey.REPLACE_VALUE_BY_EXPRESSION, keywordCommand.commandKey)
        Assertions.assertEquals(2, keywordCommand.attributeGroups.size)
        Assertions.assertEquals("old", keywordCommand.attribute(0, CommandAttributeKey.SEARCH_VALUE))
        Assertions.assertEquals("new", keywordCommand.attribute(0, CommandAttributeKey.REPLACE_BY_EXPRESSION))
        Assertions.assertEquals("old2", keywordCommand.attribute(1, CommandAttributeKey.SEARCH_VALUE))
        Assertions.assertEquals("new2", keywordCommand.attribute(1, CommandAttributeKey.REPLACE_BY_EXPRESSION))
    }

    @Test
    fun `valid multi-constraint command with minimum groups`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                @render-template [
                    templateRendererClassName="MyRenderer"
                ][
                    modelName="myModel"
                    modelExpression="modelVar"
                ]
            """.trimIndent()
        )

        val keywordCommand = KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        Assertions.assertEquals(CommandKey.RENDER_TEMPLATE, keywordCommand.commandKey)
        Assertions.assertEquals(2, keywordCommand.attributeGroups.size)
        Assertions.assertEquals("MyRenderer", keywordCommand.attribute(0, CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME))
        Assertions.assertEquals("myModel", keywordCommand.attribute(1, CommandAttributeKey.TEMPLATE_MODEL_NAME))
        Assertions.assertEquals("modelVar", keywordCommand.attribute(1, CommandAttributeKey.MODEL_EXPRESSION))
    }

    @Test
    fun `valid multi-constraint command with extra MANY groups`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                @render-template [
                    templateRendererClassName="MyRenderer"
                ][
                    modelName="model"
                    modelExpression="modelVar"
                ][
                    modelName="model2"
                    modelExpression="modelVar2"
                ]
            """.trimIndent()
        )

        val keywordCommand = KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        Assertions.assertEquals(CommandKey.RENDER_TEMPLATE, keywordCommand.commandKey)
        Assertions.assertEquals(3, keywordCommand.attributeGroups.size)
    }

    @Test
    fun `throws for multi-constraint command with too few groups`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                @render-template [
                    templateRendererClassName="MyRenderer"
                ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `throws when first group has attributes from second constraint`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                @render-template [
                    modelName="myModel"
                    modelExpression="modelVar"
                ][
                    templateRendererClassName="MyRenderer"
                ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `throws when MANY group has attributes from first constraint`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                @render-template [
                    templateRendererClassName="MyRenderer"
                ][
                    templateRendererClassName="MyRenderer"
                ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `throws for missing required attribute in second attribute group`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                @render-template [
                    templateRendererClassName="MyRenderer"
                ][
                    modelName="myModel"
                ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        }
    }

    @Test
    fun `valid move-comment with direction only`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                @move-comment [
                    direction="forward"
                ]
            """.trimIndent()
        )

        val keywordCommand = KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        Assertions.assertEquals(CommandKey.MOVE_COMMENT, keywordCommand.commandKey)
        Assertions.assertEquals("forward", keywordCommand.attribute(CommandAttributeKey.DIRECTION))
    }

    @Test
    fun `valid move-comment with direction and one occurrence attribute`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                @move-comment [
                    direction="backward"
                    beforeFirstOccurrenceOf="someText"
                ]
            """.trimIndent()
        )

        val keywordCommand = KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        Assertions.assertEquals(CommandKey.MOVE_COMMENT, keywordCommand.commandKey)
        Assertions.assertEquals("someText", keywordCommand.attribute(CommandAttributeKey.BEFORE_FIRST_OCCURRENCE_OF))
    }

    @Test
    fun `throws when move-comment has two mutually exclusive occurrence attributes`() {
        val commandStructure = createSingleTemplateComment(
            comment = """
                @move-comment [
                    direction="forward"
                    beforeFirstOccurrenceOf="someText"
                    afterFirstOccurrenceOf="otherText"
                ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            KeywordCommandFactory.createKeywordCommand(commandStructure, stubLineNumbers)
        }
    }

    private val stubLineNumbers = LineNumbers.EMPTY_LINE_NUMBERS

    private fun createSingleTemplateComment(comment: String): CommandStructure {
        return TemplateCommentParser.parseComment(comment).single()
    }
}
