package org.codeblessing.typicaltemplate.contentparsing.fragmenter

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.commentparser.StructuredComment
import org.codeblessing.typicaltemplate.contentparsing.commentparser.TemplateCommentParser
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FragmentFactoryTest {

    @Test
    fun `valid text fragment is created`() {
        val commandFragment = FragmentFactory.createTextFragment("my content", stubLineNumbers)
        Assertions.assertEquals("my content", commandFragment.text)
    }

    @Test
    fun `valid command fragment is created`() {
        val templateComment = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererClassName="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ]
            """.trimIndent()
        )

        val commandFragment = FragmentFactory.createCommandFragment(templateComment, stubLineNumbers)
        val keywordCommand = commandFragment.keywordCommand
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
        val templateComment = createSingleTemplateComment(
            comment = """ 
                        @unknown
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            FragmentFactory.createCommandFragment(templateComment, stubLineNumbers)
        }
    }

    @Test
    fun `throws for too few attribute groups`() {
        val templateComment = createSingleTemplateComment(
            comment = """ 
                        @template-renderer
            """.trimIndent()
        )


        assertThrows<TemplateParsingException> {
            FragmentFactory.createCommandFragment(templateComment, stubLineNumbers)
        }
    }

    @Test
    fun `throws for too many attribute groups`() {
        val templateComment = createSingleTemplateComment(
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
            FragmentFactory.createCommandFragment(templateComment, stubLineNumbers)
        }
    }

    @Test
    fun `throws for unknown attribute key`() {
        val templateComment = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererClassNameUnknown="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            FragmentFactory.createCommandFragment(templateComment, stubLineNumbers)
        }
    }

    @Test
    fun `throws for unallowed attribute key`() {
        val templateComment = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererClassName="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                            replacement="foo"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            FragmentFactory.createCommandFragment(templateComment, stubLineNumbers)
        }
    }

    @Test
    @Disabled("Enable this test as soon as there are values (like boolean true/false)")
    fun `throws for unallowed attribute value`() {
        val templateComment = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererClassName="MyTemplate"
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            FragmentFactory.createCommandFragment(templateComment, stubLineNumbers)
        }
    }

    @Test
    fun `throws for blank attribute value when not allowed`() {
        val templateComment = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererClassName=""
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            FragmentFactory.createCommandFragment(templateComment, stubLineNumbers)
        }
    }

    @Test
    fun `throws for missing required attribute`() {
        val templateComment = createSingleTemplateComment(
            comment = """ 
                        @template-renderer [
                            templateRendererPackageName="org.codeblessing.typicaltemplate.examples"
                        ]
            """.trimIndent()
        )

        assertThrows<TemplateParsingException> {
            FragmentFactory.createCommandFragment(templateComment, stubLineNumbers)
        }
    }

    private val stubLineNumbers = LineNumbers.Companion.EMPTY_LINE_NUMBERS

    private fun createSingleTemplateComment(comment: String): StructuredComment {
        return TemplateCommentParser.parseComment(comment).single()
    }
}
