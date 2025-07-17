package org.codeblessing.typicaltemplate

import org.codeblessing.typicaltemplate.CommandAttributeKey.*

enum class CommandKey(
    val keyword: String,
    val attributeGroupConstraint: AttributeGroupConstraint = AttributeGroupConstraint.NO_ATTRIBUTES,
    val requiredAttributes: Set<CommandAttributeKey> = emptySet(),
    val optionalAttributes: Set<CommandAttributeKey> = emptySet(),
    val correspondingOpeningCommandKey: CommandKey? = null,

    ) {
    TEMPLATE(
        keyword = "template",
        attributeGroupConstraint = AttributeGroupConstraint.ONE_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(TEMPLATE_CLASS_NAME, TEMPLATE_MODEL_CLASS_NAME),
        optionalAttributes = setOf(TEMPLATE_CLASS_PACKAGE_NAME, TEMPLATE_MODEL_CLASS_PACKAGE_NAME),
    ),
    REPLACE_VALUE_BY_FIELD(
        keyword = "replace-value-by-field",
        attributeGroupConstraint = AttributeGroupConstraint.MANY_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(SEARCH_VALUE, REPLACE_BY_FIELD_NAME),
        optionalAttributes = setOf(),
    ),
    END_REPLACE_VALUE_BY_FIELD(
        keyword = "end-replace-value-by-field",
        correspondingOpeningCommandKey = REPLACE_VALUE_BY_FIELD,
    ),
    IF_FIELD(
        keyword = "if-field",
        attributeGroupConstraint = AttributeGroupConstraint.ONE_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(CONDITION_FIELD_NAME),
        optionalAttributes = setOf(),
    ),
    END_IF_FIELD(
        keyword = "end-if-field",
        correspondingOpeningCommandKey = IF_FIELD,
    ),
    ;

    companion object {
        fun fromKeyword(keyword: String): CommandKey? {
            return entries.firstOrNull { it.keyword == keyword }
        }
    }
    val correspondingClosingCommandKey: CommandKey?
        get() = entries.singleOrNull { it.correspondingOpeningCommandKey == this }

    val isOpeningCommand: Boolean
        get() = correspondingClosingCommandKey != null
    val isClosingCommand: Boolean
        get() = this.correspondingOpeningCommandKey != null

    val allowedAttributes: Set<CommandAttributeKey> = requiredAttributes + optionalAttributes
    val unallowedAttributes: Set<CommandAttributeKey> = CommandAttributeKey.entries.toMutableSet() - allowedAttributes

    fun missingRequiredAttributes(presentAttributes: Set<CommandAttributeKey>): Set<CommandAttributeKey> {
        val missingAttributes = requiredAttributes.toMutableSet()
        missingAttributes.removeAll(presentAttributes)
        return missingAttributes
    }

    fun unallowedAttributes(presentAttributes: Set<CommandAttributeKey>): Set<CommandAttributeKey> {
        return unallowedAttributes.intersect(presentAttributes)
    }
}
