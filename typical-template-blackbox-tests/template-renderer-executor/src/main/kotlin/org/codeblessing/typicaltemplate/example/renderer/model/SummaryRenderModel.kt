package org.codeblessing.typicaltemplate.example.renderer.model

data class SummaryRenderModel(
    val summaryClassName: String,
    val fields: List<SummaryFieldRenderModel>,
) {
    val summaryPackageName: String = "my.example.businessproject.summary"
    val summaryFileName: String = "${summaryClassName}.kt"
    val summaryExtensionsFileName: String = "${summaryClassName}Extensions.kt"
    val summaryPackageDirectory: String = summaryPackageName.replace(".", "/")
}
