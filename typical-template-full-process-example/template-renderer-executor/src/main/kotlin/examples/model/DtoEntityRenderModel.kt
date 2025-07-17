package examples.model

import java.util.Locale
import java.util.Locale.getDefault

data class DtoEntityRenderModel(
    val entityName: String
) {
    val entityPrimaryField: String = entityName.replaceFirstChar { it.lowercase(getDefault()) }
    val dtoPackageName: String = "org.codeblessing.typicaltemplate.example"
    val kotlinDtoClassName: String = "${entityName}Dto"
    val kotlinDtoFileName: String = "${kotlinDtoClassName}.kt"
    val dtoNestedPackageDirectory: String = dtoPackageName.replace(".", "/")
}
