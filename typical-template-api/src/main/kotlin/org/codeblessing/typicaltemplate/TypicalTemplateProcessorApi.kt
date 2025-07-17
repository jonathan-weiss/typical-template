package org.codeblessing.typicaltemplate

import java.nio.file.Path

interface TypicalTemplateProcessorApi {

    fun processTypicalTemplate(templatingConfigurations: List<TemplatingConfiguration>): Map<TemplatingConfiguration, List<Path>>
}
