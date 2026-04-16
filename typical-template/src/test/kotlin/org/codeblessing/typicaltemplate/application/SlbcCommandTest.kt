package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class SlbcCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @slbc command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <div>Hello</div><!-- @tt{{{ @slbc }}}@ -->
            <div>World</div>
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "SlbcCommandTest.txt.kt")
    }
}
