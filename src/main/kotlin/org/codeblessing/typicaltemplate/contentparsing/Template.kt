package org.codeblessing.typicaltemplate.contentparsing

data class Template(
    val templateClassName: String,
    val templateClassPackage: String,
    val modelClassName: String,
    val modelClassPackage: String,

    val templateFragments: List<TemplateFragment>,
) {
    val modelName: String = "model"
}
