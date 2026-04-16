package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class ModifyProvidedFilenameByReplacementsCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @modify-provided-filename-by-replacements command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <!-- @tt{{{
               @replace-value-by-value [ searchValue="my-renderer" replaceByValue="your-renderer" ]
               @modify-provided-filename-by-replacements
            }}}@ -->
            <div>Hello</div>
            <!-- @tt{{{ @end-replace-value-by-value }}}@ -->
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "ModifyProvidedFilenameByReplacementsCommandTest.txt.kt")
    }
}
