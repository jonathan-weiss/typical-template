package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class ForeachCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @foreach command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <!-- @tt{{{
               @template-model [ modelClassName="Team" modelPackageName="com.example.model" modelName="team" ]
            }}}@ -->
            <html>
            <!-- @tt{{{ @foreach [ iteratorExpression="team.members" loopVariable="theMember" ] }}}@ -->
            <!-- @tt{{{ @replace-value-by-expression [ searchValue="John" replaceByExpression="theMember" ] }}}@ -->
            
            <div>Member: John</div>
            <!-- @tt{{{ @end-replace-value-by-expression }}}@ -->
            <!-- @tt{{{ @end-foreach }}}@ -->
            </html>
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "ForeachCommandTest.txt")
    }
}
