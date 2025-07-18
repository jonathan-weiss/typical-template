package org.codeblessing.typicaltemplate.example.renderer.model

data class DtoFieldRenderModel(
    val fieldName: String,
    val fieldTypeName: String,
    val isNullable: Boolean,
)
