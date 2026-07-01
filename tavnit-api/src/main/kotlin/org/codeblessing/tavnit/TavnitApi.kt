package org.codeblessing.tavnit

import java.nio.file.Path
import java.util.ServiceLoader

object TavnitApi {

    fun runTavnit(templatingConfigurations: List<TemplatingConfiguration>): Map<TemplatingConfiguration, List<Path>> {
        val tavnitApis: ServiceLoader<TavnitProcessorApi> = ServiceLoader.load(TavnitProcessorApi::class.java)

        val tavnitApi = requireNotNull(tavnitApis.firstOrNull()) {
            "Could not find an implementation of the interface '${TavnitProcessorApi::class}'."
        }
        return tavnitApi.processTavnit(templatingConfigurations)
    }
}
