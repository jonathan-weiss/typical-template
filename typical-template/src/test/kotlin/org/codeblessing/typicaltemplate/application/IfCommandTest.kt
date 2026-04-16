package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class IfCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @if command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <!-- @tt{{{
               @template-model [ modelClassName="Person" modelPackageName="com.example.model" modelName="person" ]
            }}}@ -->
            <!-- @tt{{{ @if [ conditionExpression="person.isAdmin()" ] }}}@ -->
            <div>Admin Content</div>
            <!-- @tt{{{ @end-if }}}@ -->
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "IfCommandTest.txt.kt")
    }

    @Test
    fun `test parsing @else command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <!-- @tt{{{
               @template-model [ modelClassName="Person" modelPackageName="com.example.model" modelName="person" ]
            }}}@ -->
            <!-- @tt{{{ @if [ conditionExpression="person.isAdmin()" ] }}}@ -->
            <div>Admin Content</div>
            <!-- @tt{{{ @else }}}@ -->
            <div>User Content</div>
            <!-- @tt{{{ @end-if }}}@ -->
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "IfCommandTest-withElse.txt.kt")
    }

    @Test
    fun `test parsing @else-if command`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <!-- @tt{{{
               @template-model [ modelClassName="Person" modelPackageName="com.example.model" modelName="person" ]
            }}}@ -->
            <!-- @tt{{{ @if [ conditionExpression="person.isAdmin()" ] }}}@ -->
            <div>Admin Content</div>
            <!-- @tt{{{ @else-if [ conditionExpression="person.isModerator()" ] }}}@ -->
            <div>Moderator Content</div>
            <!-- @tt{{{ @end-if }}}@ -->
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "IfCommandTest-withElseIf.txt.kt")
    }
}
