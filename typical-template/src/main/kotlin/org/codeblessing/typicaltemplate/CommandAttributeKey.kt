package org.codeblessing.typicaltemplate

enum class CommandAttributeKey(val keyAsString: String) {
    TEMPLATE_RENDERER_CLASS_NAME("templateRendererClassName"),
    TEMPLATE_RENDERER_PACKAGE_NAME("templateRendererPackageName"),
    TEMPLATE_MODEL_CLASS_NAME("modelClassName"),
    TEMPLATE_MODEL_PACKAGE_NAME("modelPackageName"),
    TEMPLATE_MODEL_NAME("modelName"),
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
