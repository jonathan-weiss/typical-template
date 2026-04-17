package org.codeblessing.typicaltemplate.config

object TypicalTemplateConfigProvider {

    private val loadedConfiguration: TypicalTemplateConfig by lazy { TypicalTemplateConfigReader.readConfiguration() }

    fun getConfiguration(): TypicalTemplateConfig = loadedConfiguration
}
