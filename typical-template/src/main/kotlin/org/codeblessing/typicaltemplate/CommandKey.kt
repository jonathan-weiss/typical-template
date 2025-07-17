package org.codeblessing.typicaltemplate

import org.codeblessing.typicaltemplate.CommandAttributeKey.*

enum class CommandKey(
    val keyword: String,
    val attributeGroupConstraint: AttributeGroupConstraint = AttributeGroupConstraint.NO_ATTRIBUTES,
    val requiredAttributes: Set<CommandAttributeKey> = emptySet(),
    val optionalAttributes: Set<CommandAttributeKey> = emptySet(),
    val correspondingOpeningCommandKey: CommandKey? = null,

    ) {
    TEMPLATE_RENDERER(
        keyword = "template-renderer",
        attributeGroupConstraint = AttributeGroupConstraint.ONE_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(TEMPLATE_RENDERER_CLASS_NAME),
        optionalAttributes = setOf(TEMPLATE_RENDERER_PACKAGE_NAME),
    ),
    TEMPLATE_MODEL(
        keyword = "template-model",
        attributeGroupConstraint = AttributeGroupConstraint.MANY_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(TEMPLATE_MODEL_CLASS_NAME, TEMPLATE_MODEL_NAME),
        optionalAttributes = setOf(TEMPLATE_MODEL_PACKAGE_NAME),
    ),
    REPLACE_VALUE_BY_EXPRESSION(
        keyword = "replace-value-by-expression",
        attributeGroupConstraint = AttributeGroupConstraint.MANY_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(SEARCH_VALUE, REPLACE_BY_EXPRESSION),
        optionalAttributes = setOf(),
    ),
    END_REPLACE_VALUE_BY_EXPRESSION(
        keyword = "end-replace-value-by-expression",
        correspondingOpeningCommandKey = REPLACE_VALUE_BY_EXPRESSION,
    ),
    IF_CONDITION(
        keyword = "if-condition",
        attributeGroupConstraint = AttributeGroupConstraint.ONE_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(CONDITION_EXPRESSION),
        optionalAttributes = setOf(),
    ),
    END_IF_CONDITION(
        keyword = "end-if-condition",
        correspondingOpeningCommandKey = IF_CONDITION,
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
