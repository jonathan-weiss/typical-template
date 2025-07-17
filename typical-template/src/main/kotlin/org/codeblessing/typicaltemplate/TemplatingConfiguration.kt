package org.codeblessing.typicaltemplate

import org.codeblessing.typicaltemplate.filesearch.FileSearchLocation
import org.codeblessing.typicaltemplate.template.TemplateConfiguration

data class TemplatingConfiguration(
    val fileSearchLocations: List<FileSearchLocation>,
    val templateConfiguration: TemplateConfiguration,
)
