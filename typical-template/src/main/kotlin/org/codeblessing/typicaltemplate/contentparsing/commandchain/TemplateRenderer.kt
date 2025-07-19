package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.contentparsing.fragmenter.TemplateFragment

data class TemplateRenderer(
    val templateRendererClass: ClassDescription,
    val modelClasses: List<ModelDescription>,
    val templateFragments: List<TemplateFragment>,
) {
    val modelName: String = "model"
}
