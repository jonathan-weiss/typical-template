package org.codeblessing.typicaltemplate.application

import org.junit.jupiter.api.Test

class ReplaceValueByExpressionCommandTest: AbstractCommandTest() {

    @Test
    fun `test parsing @replace-value-by-expression command`() {
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
            <!-- @tt{{{
               @replace-value-by-expression [ searchValue="John" replaceByExpression="person.name" ]
            }}}@ -->
            <div>Hello John</div>
            <!-- @tt{{{ @end-replace-value-by-expression }}}@ -->
        """.trimIndent()

        assertExpectedGeneratedText(contentToParse, "ReplaceValueByExpressionCommandTest.txt")
    }
}
