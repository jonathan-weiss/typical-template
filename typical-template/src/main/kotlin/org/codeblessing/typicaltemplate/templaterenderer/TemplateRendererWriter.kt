package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.TemplateConfiguration
import org.codeblessing.typicaltemplate.contentparsing.Template
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

object TemplateRendererWriter {

    fun writeTemplate(template: Template, templateConfiguration: TemplateConfiguration): Path {
        val templateSourceContent = TemplateRendererContentCreator.createMultilineStringTemplateContent(template)
        val kotlinTemplateClassContent = TemplateRendererClassContentCreator.wrapInKotlinTemplateClassContent(template, templateSourceContent)
        val kotlinFilePath = template.kotlinTemplateClassFilePath(templateConfiguration.templateBaseSrcPath)
        println("Writing file ${kotlinFilePath.absolutePathString()}")
        println("--------------------")
        println(kotlinTemplateClassContent)
        println("--------------------")
        kotlinFilePath.createParentDirectories()
        kotlinFilePath.writeText(kotlinTemplateClassContent)

        return kotlinFilePath
    }
}
