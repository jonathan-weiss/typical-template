package org.codeblessing.typicaltemplate

typealias AttributeValue = String  // just for documentation/clarification

interface AllowedValue {
    val value: AttributeValue
}

inline fun <reified T : Enum<T>> AttributeValue.toEnum(): T =
    enumValues<T>().single() { it is AllowedValue && it.value == this }

enum class IsListValue(override val value: AttributeValue): AllowedValue {
    YES("yes"),
    NO("no"),
}

enum class DirectionValue(override val value: AttributeValue): AllowedValue {
    BACKWARD("backward"),
    FORWARD("forward"),
}

enum class ExpandModeValue(override val value: AttributeValue): AllowedValue {
    BLANKS("blanks"),
    LINEBREAK("linebreak"),
}
