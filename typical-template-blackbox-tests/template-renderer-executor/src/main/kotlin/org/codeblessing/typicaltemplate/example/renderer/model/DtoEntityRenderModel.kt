package org.codeblessing.typicaltemplate.example.renderer.model

import java.util.Locale

data class DtoEntityRenderModel(
    val entityName: String,
    val fields: List<DtoFieldRenderModel>,
) {
    val entityPrimaryField: String = entityName.replaceFirstChar { it.lowercase(Locale.getDefault()) }
    val dtoPackageName: String = "my.example.businessproject.dto"
    val kotlinDtoClassName: String = "${entityName}Dto"
    val kotlinDtoFileName: String = "${kotlinDtoClassName}.kt"
    val dtoNestedPackageDirectory: String = dtoPackageName.replace(".", "/")
}
