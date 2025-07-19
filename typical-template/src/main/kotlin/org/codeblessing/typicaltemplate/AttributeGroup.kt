package org.codeblessing.typicaltemplate

data class AttributeGroup(
    val attributes: Map<CommandAttributeKey, AttributeValue>,
) {
    fun attribute(key: CommandAttributeKey): AttributeValue {
        return attributes.getValue(key)
    }

    fun attributeOptional(key: CommandAttributeKey): AttributeValue? {
        return attributes[key]
    }
}
