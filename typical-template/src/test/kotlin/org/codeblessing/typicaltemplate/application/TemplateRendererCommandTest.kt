package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class TemplateRendererCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @template-renderer command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <div>Hello World</div>
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "TemplateRendererCommandTest.txt")
    }

    @Test
    fun `test parsing @end-template-renderer command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <div>Hello World</div>
            <!-- @tt{{{ @end-template-renderer }}}@ -->
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "TemplateRendererCommandTest-withEndTemplateRenderer.txt")
    }
}
