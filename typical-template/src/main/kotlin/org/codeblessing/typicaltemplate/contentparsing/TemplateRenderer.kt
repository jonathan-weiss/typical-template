package org.codeblessing.typicaltemplate.contentparsing

data class TemplateRenderer(
    val templateRendererClass: ClassDescription,
    val modelClasses: List<ModelDescription>,
    val templateFragments: List<TemplateFragment>,
) {
    val modelName: String = "model"
}
