package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.ClasspathResourceLoader
import org.codeblessing.typicaltemplate.contentparsing.ClassDescription
import org.codeblessing.typicaltemplate.contentparsing.ModelDescription
import org.codeblessing.typicaltemplate.contentparsing.TemplateRenderer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TemplateRendererClassContentCreatorTest {

    @Test
    fun `wrap template content into kotlin template object class content`() {
        val templateRenderer = TemplateRenderer(
            templateRendererClass = ClassDescription(
                className = "TemplateTest",
                classPackageName = "org.codeblessing.typicaltemplate.template",
            ),
            modelClasses = listOf(
                ModelDescription(
                    modelClassDescription = ClassDescription(
                        className = "TemplateModel",
                        classPackageName = "org.codeblessing.typicaltemplate.template.model",
                    ),
                    modelName = "model",
                )
            ),
            templateFragments = emptyList()
        )
        val kotlinClassContent = TemplateRendererClassContentCreator.wrapInKotlinTemplateClassContent(templateRenderer, "| hello world")

        val expectedContent = ClasspathResourceLoader.loadClasspathResource(
            classpathResourcePath = "org/codeblessing/typicaltemplate/templaterenderer/TemplateClassContentCreatorTest-expected-content.txt"
        )
        assertEquals(expectedContent, kotlinClassContent)
    }

}
