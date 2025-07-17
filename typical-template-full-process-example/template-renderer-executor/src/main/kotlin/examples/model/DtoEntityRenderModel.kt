package examples.model

data class DtoEntityRenderModel(
    val entityName: String
) {
    val dtoPackageName: String = "org.codeblessing.typicaltemplate.example"
    val kotlinDtoClassName: String = "${entityName}Dto"
    val kotlinDtoFileName: String = "${kotlinDtoClassName}.kt"
    val dtoNestedPackageDirectory: String = dtoPackageName.replace(".", "/")
}
