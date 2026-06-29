package org.codeblessing.typicaltemplate.blackboxtests.whitespace

import org.codeblessing.typicaltemplate.blackboxtests.AbstractBlackboxTest
import kotlin.test.Test

class WhitespaceBlackboxTest : AbstractBlackboxTest() {

    @Test
    fun `whitespace commands produce the expected output`() {
        val templateRelativePath = "whitespace/whitespace.html"
        val generatedRelativePath = "whitespace/whitespace.html"
        val expectationResource = "whitespace/whitespace.expectation.html"
        val templateRoot = webAppPath()
        assertSameContent(
            templateRoot.resolve(templateRelativePath),
            webAppGeneratedPath().resolve(generatedRelativePath),
            expectationResource,
        )
    }
}
