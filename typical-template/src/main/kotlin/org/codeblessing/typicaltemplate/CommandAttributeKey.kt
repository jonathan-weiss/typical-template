package org.codeblessing.typicaltemplate

enum class CommandAttributeKey(
    val keyAsString: String,
    val allowedValues: List<AttributeValue>? = null,
    val requireNotEmpty: Boolean = true,
) {
    TEMPLATE_RENDERER_CLASS_NAME("templateRendererClassName"),
    TEMPLATE_RENDERER_PACKAGE_NAME("templateRendererPackageName"),
    TEMPLATE_RENDERER_INTERFACE_NAME("templateRendererInterfaceName"),
    TEMPLATE_RENDERER_INTERFACE_PACKAGE_NAME("templateRendererInterfacePackageName"),
    TEMPLATE_MODEL_CLASS_NAME("modelClassName"),
    TEMPLATE_MODEL_PACKAGE_NAME("modelPackageName"),
    TEMPLATE_MODEL_NAME("modelName"),
    TEMPLATE_MODEL_IS_LIST("isList", listOf("true", "false")),
    REPLACE_BY_EXPRESSION("replaceByExpression"),
    REPLACE_BY_VALUE("replaceByValue"),
    SEARCH_VALUE("searchValue"),
    CONDITION_EXPRESSION("conditionExpression"),
    LOOP_ITERABLE_EXPRESSION("iteratorExpression"),
    LOOP_VARIABLE_NAME("loopVariable"),
    TEXT("text"),
    MODEL_EXPRESSION("modelExpression"),
    ;

    companion object {
        fun fromString(key: String): CommandAttributeKey? {
            return entries.firstOrNull { it.keyAsString == key }
        }
    }
}
