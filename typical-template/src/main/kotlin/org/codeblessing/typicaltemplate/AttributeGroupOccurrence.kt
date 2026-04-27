package org.codeblessing.typicaltemplate

enum class AttributeGroupOccurrence(
    val minNumberOfAttributeGroups: Int,
    val maxNumberOfAttributeGroups: Int,
) {
    ONE_ATTRIBUTE_GROUP(minNumberOfAttributeGroups = 1, maxNumberOfAttributeGroups = 1),
    MANY_ATTRIBUTE_GROUP(minNumberOfAttributeGroups = 1, maxNumberOfAttributeGroups = Int.MAX_VALUE),
}
