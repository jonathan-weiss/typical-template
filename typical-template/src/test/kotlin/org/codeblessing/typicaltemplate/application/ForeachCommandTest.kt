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
            <!-- @tt{{{ @foreach [ iteratorExpression="team.members" loopVariable="member" ] }}}@ -->
            <div>member</div>
            <!-- @tt{{{ @end-foreach }}}@ -->
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "ForeachCommandTest.txt")
    }
}
