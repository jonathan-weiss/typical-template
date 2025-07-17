package org.codeblessing.typicaltemplate.template

import org.codeblessing.typicaltemplate.ClasspathResourceLoader
import org.codeblessing.typicaltemplate.CommandChainBuilder
import org.codeblessing.typicaltemplate.contentparsing.Template
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TemplateContentCreatorTest {

    private val fragments = CommandChainBuilder.create()
        .addText(
            """
                this is a test A 1.
                this is a test A 2.
            """.trimIndent()
        )
        .addText(
            """
                this is a test B 1.
                this is a test B 2.
            """.trimIndent()
        )
        .addReplaceValueByFieldCommand(
            "author" to "entityNameDecapitalized",
            "Author" to "entityName",
        )
        .addText(
            """
                
                fun getAuthor(): Author {
                    return author;
                }
                """.trimIndent()
        )
        .addIfFieldCommand("isEntityNullable")
        .addText(
            """
                
                fun getAuthorNullable(): Author? {
                    return author
                }
                """.trimIndent()
        )
        .addEndIfFieldCommand()
        .addEndReplaceValueByFieldCommand()
        .addText(
            """
                
                This author and Author should not be replaced.
                """.trimIndent()
        )
        .addReplaceValueByFieldCommand(
            "author" to "entityNameDecapitalized",
            "Author" to "entityName",
        )
        .addReplaceValueByFieldCommand(
            "Author" to "entityNameCapitalized",
        )
        .addText(
            """
                
                fun getAuthor(): Author {
                    return author
                }
                """.trimIndent()
        )
        .addEndReplaceValueByFieldCommand()
        .addEndReplaceValueByFieldCommand()
        .build()

    private val expectedContent = ClasspathResourceLoader.loadClasspathResource(
        classpathResourcePath = "org/codeblessing/typicaltemplate/template/TemplateContentCreatorTest-expected-content.txt",
    )

    @Test
    fun `create template content with various commands`() {
        val kotlinClassContent = TemplateContentCreator.createMultilineStringTemplateContent(createTemplateWithFragments())
        println("---------------------")
        println(kotlinClassContent)
        println("---------------------")
        assertEquals(expectedContent, kotlinClassContent)
    }

    private fun createTemplateWithFragments(): Template =
        Template(
            templateClassName = "TemplateTest",
            templateClassPackage = "org.codeblessing.typicaltemplate.template",
            modelClassName = "TemplateModel",
            modelClassPackage = "org.codeblessing.typicaltemplate.template.model",
            templateFragments = fragments
        )
}
