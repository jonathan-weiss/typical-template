package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class TemplateRendererCommandTest: AbstractCommandTest() {

    @Test
    fun `parse template renderer command and create kotlin renderer class`() {
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
}
