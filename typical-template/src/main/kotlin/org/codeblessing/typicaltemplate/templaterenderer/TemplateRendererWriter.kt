package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.TemplateRendererConfiguration
import org.codeblessing.typicaltemplate.contentparsing.commandchain.TemplateRendererDescription
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

object TemplateRendererWriter {

    fun writeTemplate(templateRendererDescription: TemplateRendererDescription, templateRendererConfiguration: TemplateRendererConfiguration): Path {
        val kotlinMultilineContent = TemplateRendererContentCreator.createMultilineStringTemplateContent(templateRendererDescription)
        val kotlinTemplateRendererClassContent = TemplateRendererClassContentCreator.wrapInKotlinClassContent(templateRendererDescription, kotlinMultilineContent)
        val kotlinFilePath = templateRendererDescription.templateRendererClass.classFilePath(templateRendererConfiguration.templateRendererTargetSourceBasePath)

        kotlinFilePath.createParentDirectories()
        kotlinFilePath.writeText(kotlinTemplateRendererClassContent)

        return kotlinFilePath
    }
}
