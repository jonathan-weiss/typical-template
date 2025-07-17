package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.TemplateRendererConfiguration
import org.codeblessing.typicaltemplate.contentparsing.TemplateRenderer
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

object TemplateRendererWriter {

    fun writeTemplate(templateRenderer: TemplateRenderer, templateRendererConfiguration: TemplateRendererConfiguration): Path {
        val templateSourceContent = TemplateRendererContentCreator.createMultilineStringTemplateContent(templateRenderer)
        val kotlinTemplateClassContent = TemplateRendererClassContentCreator.wrapInKotlinTemplateClassContent(templateRenderer, templateSourceContent)
        val kotlinFilePath = templateRenderer.templateRendererClass.classFilePath(templateRendererConfiguration.templateRendererTargetSourceBasePath)
        println("Writing file ${kotlinFilePath.absolutePathString()}")
        println("--------------------")
        println(kotlinTemplateClassContent)
        println("--------------------")
        kotlinFilePath.createParentDirectories()
        kotlinFilePath.writeText(kotlinTemplateClassContent)

        return kotlinFilePath
    }
}
