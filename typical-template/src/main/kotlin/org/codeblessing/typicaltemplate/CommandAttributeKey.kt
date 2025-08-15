package org.codeblessing.typicaltemplate

enum class CommandAttributeKey(val keyAsString: String) {
    TEMPLATE_RENDERER_CLASS_NAME("templateRendererClassName"),
    TEMPLATE_RENDERER_PACKAGE_NAME("templateRendererPackageName"),
    TEMPLATE_MODEL_CLASS_NAME("modelClassName"),
    TEMPLATE_MODEL_PACKAGE_NAME("modelPackageName"),
    TEMPLATE_MODEL_NAME("modelName"),
    REPLACE_BY_EXPRESSION("replaceByExpression"),
    REPLACE_BY_VALUE("replaceByValue"),
    SEARCH_VALUE("searchValue"),
    CONDITION_EXPRESSION("conditionExpression"),
    LOOP_ITERABLE_EXPRESSION("iteratorExpression"),
    LOOP_VARIABLE_NAME("loopVariable"),
    TEXT("text"),
    ;

    companion object {
        fun fromString(key: String): CommandAttributeKey? {
            return entries.firstOrNull { it.keyAsString == key }
        }
    }
    val allowedValues: List<AttributeValue>? = null
    val requireNotEmpty: Boolean = true

}
