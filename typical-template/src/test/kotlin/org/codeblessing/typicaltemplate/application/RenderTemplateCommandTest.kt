package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class RenderTemplateCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @render-template command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <!-- @tt{{{
               @template-model [ modelClassName="Person" modelPackageName="com.example.model" modelName="person" ]
            }}}@ -->
            <!-- @tt{{{
               @render-template [ templateRendererClassName="SubRenderer" templateRendererPackageName="com.example.sub" ]
                                [ modelName="person" modelExpression="person" ]
            }}}@ -->
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "RenderTemplateCommandTest.txt.kt")
    }
}
