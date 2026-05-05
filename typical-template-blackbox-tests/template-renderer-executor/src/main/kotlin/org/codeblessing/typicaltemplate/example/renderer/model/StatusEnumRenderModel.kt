package org.codeblessing.typicaltemplate.example.renderer.model

data class StatusEnumRenderModel(
    val enumName: String,
    val statusValues: List<String>,
) {
    val enumPackageName: String = "my.example.businessproject.domain"
    val enumFileName: String = "${enumName}.kt"
    val enumPackageDirectory: String = enumPackageName.replace(".", "/")
}
