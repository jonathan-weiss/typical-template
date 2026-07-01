package org.codeblessing.tavnit

data class TemplatingConfiguration(
    val fileSearchLocations: List<FileSearchLocation>,
    val templateRendererConfiguration: TemplateRendererConfiguration,
)
