package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class TemplateModelCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @template-model command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <!-- @tt{{{
               @template-model [
                modelClassName="Person"
                modelPackageName="com.example.model"
                modelName="person"
               ]
            }}}@ -->
            <div>Hello</div>
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "TemplateModelCommandTest.txt.kt")
    }
}
