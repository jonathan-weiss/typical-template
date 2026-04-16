package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class IgnoreTextCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @ignore-text command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <div>Visible Content</div>
            <!-- @tt{{{ @ignore-text }}}@ -->
            <div>Hidden Content</div>
            <!-- @tt{{{ @end-ignore-text }}}@ -->
            <div>Also Visible</div>
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "IgnoreTextCommandTest.txt.kt")
    }
}
