package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.ClasspathResourceLoader
import org.codeblessing.typicaltemplate.contentparsing.ContentParser
import org.codeblessing.typicaltemplate.filemapping.CommentStyles.KOTLIN_COMMENT_STYLES
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TemplateRendererTest {

    @Test
    fun `create template content from file`() {
        val contentToParse = ClasspathResourceLoader.loadClasspathResource(
            classpathResourcePath = "org/codeblessing/typicaltemplate/templaterenderer/TemplateRendererTest-content-to-parse.kt.txt",
        )

        val expectedContent = ClasspathResourceLoader.loadClasspathResource(
            classpathResourcePath = "org/codeblessing/typicaltemplate/templaterenderer/TemplateRendererTest-expected-content.txt",
        )


        val templates = ContentParser.parseContent(content = contentToParse, KOTLIN_COMMENT_STYLES)

        assertEquals(1, templates.size)

        val template = templates.single()
        val templateSourceContent = TemplateRendererContentCreator.createMultilineStringTemplateContent(template)
        val kotlinTemplateRendererClassContent = TemplateRendererClassContentCreator.wrapInKotlinClassContent(template, templateSourceContent)

        println("--- Kotlin Template ------------------")
        println(kotlinTemplateRendererClassContent)
        println("--------------------------------------")
        assertEquals(expectedContent, kotlinTemplateRendererClassContent)
    }
}
