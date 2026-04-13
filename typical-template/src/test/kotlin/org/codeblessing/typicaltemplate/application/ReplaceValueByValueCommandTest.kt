package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class ReplaceValueByValueCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @replace-value-by-value command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <!-- @tt{{{
               @replace-value-by-value [ searchValue="John" replaceByValue="Jane" ]
            }}}@ -->
            <div>Hello John</div>
            <!-- @tt{{{ @end-replace-value-by-value }}}@ -->
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "ReplaceValueByValueCommandTest.txt")
    }
}
