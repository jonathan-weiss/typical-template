package org.codeblessing.typicaltemplate

data class TemplatingConfiguration(
    val fileSearchLocations: List<FileSearchLocation>,
    val templateConfiguration: TemplateConfiguration,
)
