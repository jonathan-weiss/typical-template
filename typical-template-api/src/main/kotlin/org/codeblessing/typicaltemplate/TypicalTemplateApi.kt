package org.codeblessing.typicaltemplate

import java.nio.file.Path
import java.util.ServiceLoader

object TypicalTemplateApi {

    fun runTypicalTemplate(templatingConfigurations: List<TemplatingConfiguration>): Map<TemplatingConfiguration, List<Path>> {
        val typicalTemplateApis: ServiceLoader<TypicalTemplateProcessorApi> = ServiceLoader.load(TypicalTemplateProcessorApi::class.java)

        val typicalTemplateApi = requireNotNull(typicalTemplateApis.firstOrNull()) {
            "Could not find an implementation of the interface '${TypicalTemplateProcessorApi::class}'."
        }
        return typicalTemplateApi.processTypicalTemplate(templatingConfigurations)
    }
}
