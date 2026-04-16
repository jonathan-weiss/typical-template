package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class PrintTextCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @print-text command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <!-- @tt{{{ @print-text [ text="Hello World" ] }}}@ -->
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "PrintTextCommandTest.txt.kt")
    }
}
