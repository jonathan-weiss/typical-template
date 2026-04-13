package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class SlacCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @slac command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <!-- @tt{{{ @slac }}}@ -->
            <div>Hello</div>
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "SlacCommandTest.txt")
    }
}
