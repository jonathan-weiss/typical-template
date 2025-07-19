package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.ClasspathResourceLoader
import org.codeblessing.typicaltemplate.contentparsing.ContentParser
import org.codeblessing.typicaltemplate.filemapping.CommentStyles.KOTLIN_COMMENT_STYLES
import org.junit.jupiter.api.Assertions
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


        val template = templates.single()
        val templateSourceContent = TemplateRendererContentCreator.createMultilineStringTemplateContent(template)
        val kotlinTemplateClassContent = TemplateRendererClassContentCreator.wrapInKotlinTemplateClassContent(template, templateSourceContent)

        println("--- Kotlin Template ------------------")
        println(kotlinTemplateClassContent)
        println("--------------------------------------")
        Assertions.assertEquals(expectedContent, kotlinTemplateClassContent)
    }
}
