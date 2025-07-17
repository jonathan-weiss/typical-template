package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.ClasspathResourceLoader
import org.codeblessing.typicaltemplate.CommandChainBuilder
import org.codeblessing.typicaltemplate.contentparsing.ClassDescription
import org.codeblessing.typicaltemplate.contentparsing.ModelDescription
import org.codeblessing.typicaltemplate.contentparsing.TemplateRenderer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TemplateRendererContentCreatorTest {

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
        classpathResourcePath = "org/codeblessing/typicaltemplate/templaterenderer/TemplateContentCreatorTest-expected-content.txt",
    )

    @Test
    fun `create template content with various commands`() {
        val kotlinClassContent = TemplateRendererContentCreator.createMultilineStringTemplateContent(createTemplateWithFragments())
        println("---------------------")
        println(kotlinClassContent)
        println("---------------------")
        assertEquals(expectedContent, kotlinClassContent)
    }

    private fun createTemplateWithFragments(): TemplateRenderer =
        TemplateRenderer(
            templateRendererClass = ClassDescription(
                className = "TemplateTest",
                classPackageName = "org.codeblessing.typicaltemplate.template",
            ),
            modelClasses = listOf(
                ModelDescription(
                    modelClassDescription = ClassDescription(
                        className = "TemplateModel",
                        classPackageName = "org.codeblessing.typicaltemplate.template",
                    ),
                    modelName = "model",
                )
            ),
            templateFragments = fragments,
        )
}
