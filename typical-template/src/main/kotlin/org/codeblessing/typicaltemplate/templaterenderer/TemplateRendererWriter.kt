package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.TemplateRendererConfiguration
import org.codeblessing.typicaltemplate.contentparsing.commandchain.TemplateRendererDescription
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

object TemplateRendererWriter {

    fun writeTemplate(
        filepathString: String,
        templateRendererDescription: TemplateRendererDescription,
        templateRendererConfiguration: TemplateRendererConfiguration
    ): Path {
        val kotlinTemplateContent = TemplateRendererContentCreator.createMultilineStringTemplateContent(filepathString, templateRendererDescription)
        val kotlinTemplateRendererClassContent = TemplateRendererClassContentCreator.wrapInKotlinClassContent(templateRendererDescription, kotlinTemplateContent)
        val kotlinFilePath = templateRendererDescription.templateRendererClass.classFilePath(templateRendererConfiguration.templateRendererTargetSourceBasePath)

        kotlinFilePath.createParentDirectories()
        kotlinFilePath.writeText(kotlinTemplateRendererClassContent)

        return kotlinFilePath
    }
}
