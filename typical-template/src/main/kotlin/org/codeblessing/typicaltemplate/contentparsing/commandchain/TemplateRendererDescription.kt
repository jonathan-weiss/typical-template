package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart

data class TemplateRendererDescription(
    val templateRendererClass: ClassDescription,
    val templateRendererInterface: ClassDescription?,
    val modelClasses: List<ModelDescription>,
    val templateChain: List<TemplateContentPart>,
)
