package org.codeblessing.typicaltemplate.blackboxtests

import org.junit.jupiter.api.Test

class KotlinSummaryBlackboxTest : AbstractBlackboxTest() {

    @Test
    fun `test output of kotlin summary class template commands`() {
        assertSameContent(
            kotlinSourcePath().resolve("my/example/businessproject/summary/OrderSummary.kt"),
            kotlinGeneratedPath().resolve("my/example/businessproject/summary/PaymentSummary.kt"),
            "paymentsummary-class.expectation.kt",
        )
    }

    @Test
    fun `test output of kotlin summary extension template commands`() {
        assertSameContent(
            kotlinSourcePath().resolve("my/example/businessproject/summary/OrderSummary.kt"),
            kotlinGeneratedPath().resolve("my/example/businessproject/summary/PaymentSummaryExtensions.kt"),
            "paymentsummary-extensions.expectation.kt",
        )
    }
}
