package org.codeblessing.typicaltemplate

enum class AttributeGroupConstraint(
    val minNumberOfAttributeGroups: Int = 0,
    val maxNumberOfAttributeGroups: Int = 0,
) {
    NO_ATTRIBUTES(minNumberOfAttributeGroups = 0, maxNumberOfAttributeGroups = 0),
    ONE_ATTRIBUTE_GROUP(minNumberOfAttributeGroups = 1, maxNumberOfAttributeGroups = 1),
    MANY_ATTRIBUTE_GROUP(minNumberOfAttributeGroups = 1, maxNumberOfAttributeGroups = Int.MAX_VALUE),
    HEADER_WITH_MANY_ATTRIBUTE_GROUPS(minNumberOfAttributeGroups = 2, maxNumberOfAttributeGroups = Int.MAX_VALUE),
}
