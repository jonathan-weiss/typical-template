package org.codeblessing.typicaltemplate

import org.codeblessing.typicaltemplate.CommandAttributeKey.*

enum class CommandKey(
    val keyword: String,
    val attributeGroupConstraint: AttributeGroupConstraint = AttributeGroupConstraint.NO_ATTRIBUTES,
    val requiredAttributes: Set<CommandAttributeKey> = emptySet(),
    val optionalAttributes: Set<CommandAttributeKey> = emptySet(),
    val correspondingOpeningCommandKey: CommandKey? = null,
    val directlyNestedInsideCommandKey: CommandKey? = null,
    val isAutoclosingSupported: Boolean = false,

    ) {
    TEMPLATE_RENDERER(
        keyword = "template-renderer",
        attributeGroupConstraint = AttributeGroupConstraint.ONE_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(TEMPLATE_RENDERER_CLASS_NAME),
        optionalAttributes = setOf(TEMPLATE_RENDERER_PACKAGE_NAME, TEMPLATE_RENDERER_INTERFACE_NAME, TEMPLATE_RENDERER_INTERFACE_PACKAGE_NAME),
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
        isAutoclosingSupported = true,
    ),
    END_REPLACE_VALUE_BY_EXPRESSION(
        keyword = "end-replace-value-by-expression",
        correspondingOpeningCommandKey = REPLACE_VALUE_BY_EXPRESSION,
    ),
    REPLACE_VALUE_BY_VALUE(
        keyword = "replace-value-by-value",
        attributeGroupConstraint = AttributeGroupConstraint.MANY_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(SEARCH_VALUE, REPLACE_BY_VALUE),
        optionalAttributes = setOf(),
        isAutoclosingSupported = true,
    ),
    END_REPLACE_VALUE_BY_VALUE(
        keyword = "end-replace-value-by-value",
        correspondingOpeningCommandKey = REPLACE_VALUE_BY_VALUE,
    ),
    IF_CONDITION(
        keyword = "if",
        attributeGroupConstraint = AttributeGroupConstraint.ONE_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(CONDITION_EXPRESSION),
        optionalAttributes = setOf(),
    ),
    ELSE_IF_CONDITION(
        keyword = "else-if",
        attributeGroupConstraint = AttributeGroupConstraint.ONE_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(CONDITION_EXPRESSION),
        optionalAttributes = setOf(),
        directlyNestedInsideCommandKey = IF_CONDITION,
    ),
    ELSE_CLAUSE(
        keyword = "else",
        directlyNestedInsideCommandKey = IF_CONDITION,
    ),
    END_IF_CONDITION(
        keyword = "end-if",
        correspondingOpeningCommandKey = IF_CONDITION,
    ),
    FOREACH(
        keyword = "foreach",
        attributeGroupConstraint = AttributeGroupConstraint.ONE_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(LOOP_ITERABLE_EXPRESSION, LOOP_VARIABLE_NAME),
        optionalAttributes = setOf(),
    ),
    END_FOREACH(
        keyword = "end-foreach",
        correspondingOpeningCommandKey = FOREACH,
    ),
    IGNORE_TEXT(
        keyword = "ignore-text",
        isAutoclosingSupported = true,
    ),
    END_IGNORE_TEXT(
        keyword = "end-ignore-text",
        correspondingOpeningCommandKey = IGNORE_TEXT,
    ),
    PRINT_TEXT(
        keyword = "print-text",
        attributeGroupConstraint = AttributeGroupConstraint.ONE_ATTRIBUTE_GROUP,
        requiredAttributes = setOf(TEXT),
    ),
    STRIP_LINE_BEFORE_COMMENT(
        keyword = "slbc",
        attributeGroupConstraint = AttributeGroupConstraint.NO_ATTRIBUTES,
    ),
    STRIP_LINE_AFTER_COMMENT(
        keyword = "slac",
        attributeGroupConstraint = AttributeGroupConstraint.NO_ATTRIBUTES,
    ),
    MODIFY_PROVIDED_FILENAME_BY_REPLACEMENTS(
        keyword = "modify-provided-filename-by-replacements",
        attributeGroupConstraint = AttributeGroupConstraint.NO_ATTRIBUTES,
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
    val isRequiredDirectlyNestedInOtherCommand: Boolean
        get() = this.directlyNestedInsideCommandKey != null

    val correspondingOpeningCommandKeyForAutoclose: CommandKey?
        get() = correspondingOpeningCommandKey ?: directlyNestedInsideCommandKey

    val isTriggerAutoclose: Boolean
        get() = this.correspondingOpeningCommandKeyForAutoclose != null


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
