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
            templateRendererInterface = null,
            modelClasses = listOf(
                ModelDescription(
                    modelClassDescription = ClassDescription(
                        className = "TemplateModel",
                        classPackageName = "org.codeblessing.typicaltemplate.template.model",
                    ),
                    modelName = "model",
                    isList = false,
                ),
                ModelDescription(
                    modelClassDescription = ClassDescription(
                        className = "TemplateModel",
                        classPackageName = "org.codeblessing.typicaltemplate.template.model",
                    ),
                    modelName = "models",
                    isList = true,
                )
            ),
            templateChain = emptyList()
        )
        val kotlinTemplateContent = KotlinTemplateContent(
            rendererCode = "| hello world",
            filepath = "dum-dir/dum-sub-dir/dummy.txt",
        )
        val kotlinClassContent = TemplateRendererClassContentCreator.wrapInKotlinClassContent(templateRendererDescription, kotlinTemplateContent)

        val expectedContent = ClasspathResourceLoader.loadClasspathResource(
            classpathResourcePath = "org/codeblessing/typicaltemplate/templaterenderer/TemplateRendererClassContentCreatorTest-expected-content.txt"
        )
        assertEquals(expectedContent, kotlinClassContent)
    }

}
