package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.ClasspathResourceLoader
import org.codeblessing.typicaltemplate.contentparsing.ContentParser
import org.codeblessing.typicaltemplate.filemapping.CommentStyles.KOTLIN_COMMENT_STYLES
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Paths

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
        val filepathString = "dummy-dir/author-subdir/dummy-Author.txt"
        val kotlinTemplateContent = TemplateRendererContentCreator.createMultilineStringTemplateContent(filepathString, template)
        val kotlinTemplateRendererClassContent = TemplateRendererClassContentCreator.wrapInKotlinClassContent(template, kotlinTemplateContent)

        println("--- Kotlin Template ------------------")
        println(kotlinTemplateRendererClassContent)
        println("--------------------------------------")
        assertEquals(expectedContent, kotlinTemplateRendererClassContent)
    }

    @Test
    fun `create template content from file with nested template-renderers`() {
        val contentToParse = ClasspathResourceLoader.loadClasspathResource(
            classpathResourcePath = "org/codeblessing/typicaltemplate/templaterenderer/TemplateRendererTest-nested-content-to-parse.kt.txt",
        )

        val templates = ContentParser.parseContent(content = contentToParse, KOTLIN_COMMENT_STYLES)

        assertEquals(3, templates.size)

        val expectedFiles = listOf(
            "TemplateRendererTest-nested-expected-outer.txt",
            "TemplateRendererTest-nested-expected-inner.txt",
            "TemplateRendererTest-nested-expected-deep.txt",
        )
        val filepathStrings = listOf(
            "dummy-dir/author-subdir/dummy-Author.txt",
            "dummy-dir/inner.txt",
            "dummy-dir/deep.txt",
        )

        templates.forEachIndexed { index, template ->
            val kotlinTemplateContent = TemplateRendererContentCreator.createMultilineStringTemplateContent(filepathStrings[index], template)
            val kotlinTemplateRendererClassContent = TemplateRendererClassContentCreator.wrapInKotlinClassContent(template, kotlinTemplateContent)

            println("--- Kotlin Template [${template.templateRendererClass.className}] ------------------")
            println(kotlinTemplateRendererClassContent)
            println("--------------------------------------")

            val expectedContent = ClasspathResourceLoader.loadClasspathResource(
                classpathResourcePath = "org/codeblessing/typicaltemplate/templaterenderer/${expectedFiles[index]}",
            )
            assertEquals(expectedContent, kotlinTemplateRendererClassContent)
        }
    }
}
