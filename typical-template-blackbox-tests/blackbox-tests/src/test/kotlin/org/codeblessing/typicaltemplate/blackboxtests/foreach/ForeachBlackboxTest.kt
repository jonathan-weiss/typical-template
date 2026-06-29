package org.codeblessing.typicaltemplate.blackboxtests.foreach

import org.codeblessing.typicaltemplate.blackboxtests.AbstractBlackboxTest
import org.junit.jupiter.api.Test

class ForeachBlackboxTest: AbstractBlackboxTest() {

    @Test
    fun `test output of several typical template commands`() {
        assertSameContent(
            htmlSourcePath().resolve("foreach.html"),
            htmlGeneratedPath().resolve("gardening.html"),
            "foreach.expectation.html")
    }
}
