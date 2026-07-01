package org.codeblessing.tavnit

import java.nio.file.Path

interface TavnitProcessorApi {

    fun processTavnit(templatingConfigurations: List<TemplatingConfiguration>): Map<TemplatingConfiguration, List<Path>>
}
