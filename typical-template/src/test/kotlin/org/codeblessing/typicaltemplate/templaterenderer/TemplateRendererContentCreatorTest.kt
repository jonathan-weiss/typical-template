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
        .addReplaceValueByExpressionCommand(
            "author" to "model.entityNameDecapitalized",
            "Author" to "model.entityName",
        )
        .addText(
            """
                
                fun getAuthor(): Author {
                    return author;
                }
                """.trimIndent()
        )
        .addIfCommand("model.isEntityNullable()")
        .addText(
            """
                
                fun getAuthorNullable(): Author? {
                    return author
                }
                """.trimIndent()
        )
        .addEndIfCommand()
        .addEndReplaceValueByExpressionCommand()
        .addText(
            """
                
                This author and Author should not be replaced.
                """.trimIndent()
        )
        .addReplaceValueByExpressionCommand(
            "author" to "model.entityNameDecapitalized",
            "Author" to "entityName",
        )
        .addReplaceValueByExpressionCommand(
            "Author" to "model.entityNameCapitalized",
        )
        .addText(
            """
                
                fun getAuthor(): Author {
                    return author
                }
                """.trimIndent()
        )
        .addEndReplaceValueByExpressionCommand()
        .addEndReplaceValueByExpressionCommand()
        .addText(
            """

                // test the if..else..end-if statement

                """.trimIndent(),
        )

        .addIfCommand("model.isSerializable()")
        .addText(
            """
                
                fun isSerialize(): Boolean = true
                """.trimIndent(),
        )
        .addElseCommand()
        .addText(
            """

                fun isSerialize(): Boolean = false
                """.trimIndent(),
        )
        .addEndIfCommand()
        .addText(
            """

                // test the if..else-if..end-if statement

                """.trimIndent(),
        )
        .addText(
            """

                val visibility: String = 
                """.trimIndent(),
        )
        .addIfCommand("model.isPrivate()")
        .addText(
            """
                      "private"
                """.trimIndent(),
        )
        .addElseIfCommand("model.isProtected()")
        .addText(
            """
                      "protected"
                """.trimIndent(),
        )
        .addElseIfCommand("model.isPublic()")
        .addText(
            """
                      "public"
                """.trimIndent(),
        )
        .addEndIfCommand()
        .addText(
            """
                ) // end of characteristics list
                """.trimIndent(),
        )
        .addText(
            """

                // test the if..else-if..else..end-if statement

                """.trimIndent(),
        )
        .addText(
            """

                val mainCharacteristic: String = 
                """.trimIndent(),
        )
        .addIfCommand("model.isEnum()")
        .addText(
            """
                      "enum-class",
                """.trimIndent(),
        )
        .addElseIfCommand("model.isDataClass()")
        .addText(
            """
                      "data-class",
                """.trimIndent(),
        )
        .addElseCommand()
        .addText(
            """
                      "regular-class",
                """.trimIndent(),
        )
        .addEndIfCommand()
        .build()

    private val expectedContent = ClasspathResourceLoader.loadClasspathResource(
        classpathResourcePath = "org/codeblessing/typicaltemplate/templaterenderer/TemplateContentCreatorTest-expected-content.txt",
    )

    @Test
    fun `create template content with various commands`() {
        val kotlinClassContent =
            TemplateRendererContentCreator.createMultilineStringTemplateContent(createTemplateWithFragments())
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
                ),
            ),
            templateFragments = fragments,
        )
}
