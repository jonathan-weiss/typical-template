package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.AttributeGroup
import org.codeblessing.typicaltemplate.AttributeValue
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey

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
