package org.codeblessing.typicaltemplate

enum class CommandAttributeKey(val keyAsString: String) {
    TEMPLATE_CLASS_NAME("templateClassName"),
    TEMPLATE_CLASS_PACKAGE_NAME("templateClassPackageName"),
    TEMPLATE_MODEL_CLASS_NAME("templateModelClassName"),
    TEMPLATE_MODEL_CLASS_PACKAGE_NAME("templateModelClassPackageName"),
    REPLACE_BY_FIELD_NAME("replaceByFieldName"),
    SEARCH_VALUE("searchValue"),
    CONDITION_FIELD_NAME("conditionField"),
    ;

    companion object {
        fun fromString(key: String): CommandAttributeKey? {
            return entries.firstOrNull { it.keyAsString == key }
        }
    }
    val allowedValues: List<AttributeValue>? = null
    val requireNotEmpty: Boolean = true

}
