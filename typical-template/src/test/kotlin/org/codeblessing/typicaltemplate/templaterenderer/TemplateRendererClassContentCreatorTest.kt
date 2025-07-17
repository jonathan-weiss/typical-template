package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.ClasspathResourceLoader
import org.codeblessing.typicaltemplate.contentparsing.Template
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TemplateRendererClassContentCreatorTest {

    @Test
    fun `wrap template content into kotlin template object class content`() {
        val template = Template(
            templateClassName = "TemplateTest",
            templateClassPackage = "org.codeblessing.typicaltemplate.template",
            modelClassName = "TemplateModel",
            modelClassPackage = "org.codeblessing.typicaltemplate.template.model",
            templateFragments = emptyList()
        )
        val kotlinClassContent = TemplateRendererClassContentCreator.wrapInKotlinTemplateClassContent(template, "| hello world")

        val expectedContent = ClasspathResourceLoader.loadClasspathResource(
            classpathResourcePath = "org/codeblessing/typicaltemplate/templaterenderer/TemplateClassContentCreatorTest-expected-content.txt"
        )
        assertEquals(expectedContent, kotlinClassContent)
    }

}
