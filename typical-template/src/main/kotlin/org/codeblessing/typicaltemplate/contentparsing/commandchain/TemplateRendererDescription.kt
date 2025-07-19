package org.codeblessing.typicaltemplate.contentparsing.commandchain

data class TemplateRendererDescription(
    val templateRendererClass: ClassDescription,
    val modelClasses: List<ModelDescription>,
    val templateChain: List<ChainItem>,
) {
}
