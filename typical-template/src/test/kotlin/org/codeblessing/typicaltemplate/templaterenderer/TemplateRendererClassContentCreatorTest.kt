package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.ClasspathResourceLoader
import org.codeblessing.typicaltemplate.contentparsing.commandchain.ClassDescription
import org.codeblessing.typicaltemplate.contentparsing.commandchain.ModelDescription
import org.codeblessing.typicaltemplate.contentparsing.commandchain.TemplateRendererDescription
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TemplateRendererClassContentCreatorTest {

    @Test
    fun `wrap template content into kotlin template object class content`() {
        val templateRendererDescription = TemplateRendererDescription(
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
            templateChain = emptyList()
        )
        val kotlinClassContent = TemplateRendererClassContentCreator.wrapInKotlinTemplateClassContent(templateRendererDescription, "| hello world")

        val expectedContent = ClasspathResourceLoader.loadClasspathResource(
            classpathResourcePath = "org/codeblessing/typicaltemplate/templaterenderer/TemplateClassContentCreatorTest-expected-content.txt"
        )
        assertEquals(expectedContent, kotlinClassContent)
    }

}
