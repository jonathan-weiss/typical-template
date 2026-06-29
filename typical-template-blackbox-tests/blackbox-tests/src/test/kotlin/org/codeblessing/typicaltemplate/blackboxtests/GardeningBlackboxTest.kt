package org.codeblessing.typicaltemplate.blackboxtests

import org.junit.jupiter.api.Test

class GardeningBlackboxTest: AbstractBlackboxTest() {

    @Test
    fun `test output of several typical template commands`() {
        assertSameContent(
            htmlSourcePath().resolve("news.html"),
            htmlGeneratedPath().resolve("gardening.html"),
            "gardening.expectation.html")
    }
}
