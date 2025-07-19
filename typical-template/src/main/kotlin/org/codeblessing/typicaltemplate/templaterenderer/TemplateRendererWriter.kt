package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.TemplateRendererConfiguration
import org.codeblessing.typicaltemplate.contentparsing.commandchain.TemplateRendererDescription
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

object TemplateRendererWriter {

    fun writeTemplate(templateRendererDescription: TemplateRendererDescription, templateRendererConfiguration: TemplateRendererConfiguration): Path {
        val templateSourceContent = TemplateRendererContentCreator.createMultilineStringTemplateContent(templateRendererDescription)
        val kotlinTemplateClassContent = TemplateRendererClassContentCreator.wrapInKotlinTemplateClassContent(templateRendererDescription, templateSourceContent)
        val kotlinFilePath = templateRendererDescription.templateRendererClass.classFilePath(templateRendererConfiguration.templateRendererTargetSourceBasePath)
        println("Writing file ${kotlinFilePath.absolutePathString()}")
        println("--------------------")
        println(kotlinTemplateClassContent)
        println("--------------------")
        kotlinFilePath.createParentDirectories()
        kotlinFilePath.writeText(kotlinTemplateClassContent)

        return kotlinFilePath
    }
}
