package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.AttributeValue
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey

sealed interface TemplateFragment {
    val lineNumbers: LineNumbers
}

data class CommandFragment(
    override val lineNumbers: LineNumbers,
    val keywordCommand: KeywordCommand
) : TemplateFragment

data class TextFragment(
    override val lineNumbers: LineNumbers,
    val text: String
) : TemplateFragment


data class KeywordCommand(
    val commandKey: CommandKey,
    val attributeGroups: List<AttributeGroup>,
) {
    fun attribute(key: CommandAttributeKey): AttributeValue {
        return attributeGroups.single().attribute(key)
    }

    fun attribute(groupId: Int, key: CommandAttributeKey): AttributeValue {
        return attributeGroups[groupId].attribute(key)
    }

    fun attributeOptional(key: CommandAttributeKey): AttributeValue? {
        return attributeGroups.single().attributeOptional(key)
    }

    fun attributeOptional(groupId: Int, key: CommandAttributeKey): AttributeValue? {
        return attributeGroups[groupId].attributeOptional(key)
    }

    fun attributeGroupIndices(): IntRange {
        return attributeGroups.indices
    }

}

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

