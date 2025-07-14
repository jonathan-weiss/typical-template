package org.codeblessing.typicaltemplate.template

import org.codeblessing.typicaltemplate.contentparsing.Template
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TemplateClassContentCreatorTest {

    private val expectedContent = """
        /*
         * This file is generated using typical-template.
         */
        package org.codeblessing.typicaltemplate.template
        
        import org.codeblessing.typicaltemplate.template.model.TemplateModel
        
        /**
         * Generate the content for the template TemplateTest filled up 
         * with the content of the model [org.codeblessing.typicaltemplate.template.model.TemplateModel].
         */
        object class TemplateTest {
            
            fun renderTemplate(model: TemplateModel): String {
                return ""${'"'}
                    |hello world
                ""${'"'}.trimMargin()
            }
        }
    """.trimIndent()

    @Test
    fun `wrap template content into kotlin template object class content`() {
        val template = Template(
            templateClassName = "TemplateTest",
            templateClassPackage = "org.codeblessing.typicaltemplate.template",
            modelClassName = "TemplateModel",
            modelClassPackage = "org.codeblessing.typicaltemplate.template.model",
            templateFragments = emptyList()
        )
        val kotlinClassContent = TemplateClassContentCreator.wrapInKotlinTemplateClassContent(template, "hello world")

        assertEquals(expectedContent, kotlinClassContent)
    }

}
