package org.codeblessing.typicaltemplate

import org.codeblessing.typicaltemplate.AttributeGroupOccurrence.MANY_ATTRIBUTE_GROUP
import org.codeblessing.typicaltemplate.AttributeGroupOccurrence.ONE_ATTRIBUTE_GROUP
import org.codeblessing.typicaltemplate.CommandAttributeKey.*

enum class CommandKey(
    val keyword: String,
    val attributeGroupConstraints: List<AttributeGroupConstraint> = emptyList(),
    val correspondingOpeningCommandKey: CommandKey? = null,
    val directlyNestedInsideCommandKey: CommandKey? = null,
    val isAutoclosingSupported: Boolean = false,
) {
    TEMPLATE_RENDERER(
        keyword = "template-renderer",
        attributeGroupConstraints = listOf(
            AttributeGroupConstraint(
                occurrence = ONE_ATTRIBUTE_GROUP,
                requiredAttributes = setOf(TEMPLATE_RENDERER_CLASS_NAME),
                optionalAttributes = setOf(TEMPLATE_RENDERER_PACKAGE_NAME, TEMPLATE_RENDERER_INTERFACE_NAME, TEMPLATE_RENDERER_INTERFACE_PACKAGE_NAME),
            )
        ),
        isAutoclosingSupported = true,
    ),
    END_TEMPLATE_RENDERER(
        keyword = "end-template-renderer",
        correspondingOpeningCommandKey = TEMPLATE_RENDERER,
    ),
    TEMPLATE_MODEL(
        keyword = "template-model",
        attributeGroupConstraints = listOf(
            AttributeGroupConstraint(
                occurrence = MANY_ATTRIBUTE_GROUP,
                requiredAttributes = setOf(TEMPLATE_MODEL_CLASS_NAME, TEMPLATE_MODEL_NAME),
                optionalAttributes = setOf(TEMPLATE_MODEL_PACKAGE_NAME, TEMPLATE_MODEL_IS_LIST),
            )
        ),
    ),
    REPLACE_VALUE_BY_EXPRESSION(
        keyword = "replace-value-by-expression",
        attributeGroupConstraints = listOf(
            AttributeGroupConstraint(
                occurrence = MANY_ATTRIBUTE_GROUP,
                requiredAttributes = setOf(SEARCH_VALUE, REPLACE_BY_EXPRESSION),
            )
        ),
        isAutoclosingSupported = true,
    ),
    END_REPLACE_VALUE_BY_EXPRESSION(
        keyword = "end-replace-value-by-expression",
        correspondingOpeningCommandKey = REPLACE_VALUE_BY_EXPRESSION,
    ),
    REPLACE_VALUE_BY_VALUE(
        keyword = "replace-value-by-value",
        attributeGroupConstraints = listOf(
            AttributeGroupConstraint(
                occurrence = MANY_ATTRIBUTE_GROUP,
                requiredAttributes = setOf(SEARCH_VALUE, REPLACE_BY_VALUE),
            )
        ),
        isAutoclosingSupported = true,
    ),
    END_REPLACE_VALUE_BY_VALUE(
        keyword = "end-replace-value-by-value",
        correspondingOpeningCommandKey = REPLACE_VALUE_BY_VALUE,
    ),
    IF_CONDITION(
        keyword = "if",
        attributeGroupConstraints = listOf(
            AttributeGroupConstraint(
                occurrence = ONE_ATTRIBUTE_GROUP,
                requiredAttributes = setOf(CONDITION_EXPRESSION),
            )
        ),
    ),
    ELSE_IF_CONDITION(
        keyword = "else-if",
        attributeGroupConstraints = listOf(
            AttributeGroupConstraint(
                occurrence = ONE_ATTRIBUTE_GROUP,
                requiredAttributes = setOf(CONDITION_EXPRESSION),
            )
        ),
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
        attributeGroupConstraints = listOf(
            AttributeGroupConstraint(
                occurrence = ONE_ATTRIBUTE_GROUP,
                requiredAttributes = setOf(LOOP_ITERABLE_EXPRESSION, LOOP_VARIABLE_NAME),
            )
        ),
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
        attributeGroupConstraints = listOf(
            AttributeGroupConstraint(
                occurrence = ONE_ATTRIBUTE_GROUP,
                requiredAttributes = setOf(TEXT),
            )
        ),
    ),
    STRIP_LINE_BEFORE_COMMENT(
        keyword = "slbc",
    ),
    STRIP_LINE_AFTER_COMMENT(
        keyword = "slac",
    ),
    MODIFY_PROVIDED_FILENAME_BY_REPLACEMENTS(
        keyword = "modify-provided-filename-by-replacements",
    ),
    RENDER_TEMPLATE(
        keyword = "render-template",
        attributeGroupConstraints = listOf(
            AttributeGroupConstraint(
                occurrence = ONE_ATTRIBUTE_GROUP,
                requiredAttributes = setOf(TEMPLATE_RENDERER_CLASS_NAME),
                optionalAttributes = setOf(TEMPLATE_RENDERER_PACKAGE_NAME),
            ),
            AttributeGroupConstraint(
                occurrence = MANY_ATTRIBUTE_GROUP,
                requiredAttributes = setOf(TEMPLATE_MODEL_NAME, MODEL_EXPRESSION),
            ),
        ),
    ),
    ;

    companion object {
        fun fromKeyword(keyword: String): CommandKey? {
            return entries.firstOrNull { it.keyword == keyword }
        }
    }

    val minNumberOfAttributeGroups: Int
        get() = attributeGroupConstraints.sumOf { it.occurrence.minNumberOfAttributeGroups }

    val maxNumberOfAttributeGroups: Int
        get() = attributeGroupConstraints.fold(0) { acc, c ->
            if (acc == Int.MAX_VALUE || c.occurrence.maxNumberOfAttributeGroups == Int.MAX_VALUE) Int.MAX_VALUE
            else acc + c.occurrence.maxNumberOfAttributeGroups
        }

    val allowedAttributes: Set<CommandAttributeKey>
        get() = attributeGroupConstraints.flatMap { it.allowedAttributes }.toSet()

    private fun constraintForGroup(groupIndex: Int): AttributeGroupConstraint {
        require(attributeGroupConstraints.isNotEmpty()) {
            "constraintForGroup called on CommandKey '$keyword' which has no attribute group constraints"
        }
        return if (groupIndex < attributeGroupConstraints.size) attributeGroupConstraints[groupIndex]
        else attributeGroupConstraints.last()
    }

    fun requiredAttributesForGroup(groupIndex: Int): Set<CommandAttributeKey> =
        constraintForGroup(groupIndex).requiredAttributes

    fun allowedAttributesForGroup(groupIndex: Int): Set<CommandAttributeKey> =
        constraintForGroup(groupIndex).allowedAttributes

    fun missingRequiredAttributesForGroup(groupIndex: Int, presentAttributes: Set<CommandAttributeKey>): Set<CommandAttributeKey> {
        val required = requiredAttributesForGroup(groupIndex)
        return required.toMutableSet().apply { removeAll(presentAttributes) }
    }

    fun unallowedAttributesForGroup(groupIndex: Int, presentAttributes: Set<CommandAttributeKey>): Set<CommandAttributeKey> {
        val allowed = allowedAttributesForGroup(groupIndex)
        return presentAttributes.toMutableSet().apply { removeAll(allowed) }
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
}
