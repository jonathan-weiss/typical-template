package org.codeblessing.typicaltemplate.example.renderer.model

data class SummaryFieldRenderModel(
    val fieldName: String,
    val fieldType: String,
    val isNullable: Boolean = false,
    val isList: Boolean = false,
    val validationRules: List<String> = emptyList(),
)
