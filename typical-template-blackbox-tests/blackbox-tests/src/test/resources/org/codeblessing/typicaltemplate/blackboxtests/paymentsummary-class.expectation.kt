package my.example.businessproject.summary

// Auto-generated summary class - do not modify
@Suppress("unused")
data class PaymentSummary(
    // must not be blank
    // max 64 chars
    val paymentId: String,
    val amount: Long,
    val notes: String?,
    val tags: List<String>,
)
