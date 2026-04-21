package org.codeblessing.typicaltemplate.blackboxtests

import org.junit.jupiter.api.Test

class GardeningBlackboxTest: AbstractBlackboxTest() {

    @Test
    fun `test output of several typical template commands`() {
        assertSameContent(
            webAppPath().resolve("news.html"),
            webAppGeneratedPath().resolve("gardening.html"),
            "gardening.expectation.html")
    }
}
