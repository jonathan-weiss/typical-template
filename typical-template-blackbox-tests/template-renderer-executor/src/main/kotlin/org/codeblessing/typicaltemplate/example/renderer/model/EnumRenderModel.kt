package org.codeblessing.typicaltemplate.example.renderer.model

data class EnumRenderModel(
    val enumName: String,
    val enumValues: List<String>,
) {
    val enumPackageName: String = "my.example.businessproject.domain"
    val enumFileName: String = "${enumName}.kt"
    val enumPackageDirectory: String = enumPackageName.replace(".", "/")
}
