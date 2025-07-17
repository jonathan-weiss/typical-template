package org.codeblessing.typicaltemplate.template

import org.codeblessing.typicaltemplate.contentparsing.Template
import kotlin.io.path.absolutePathString
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

object TemplateWriter {

    fun writeTemplate(template: Template, templateConfiguration: TemplateConfiguration) {
        val templateSourceContent = TemplateContentCreator.createMultilineStringTemplateContent(template)
        val kotlinTemplateClassContent = TemplateClassContentCreator.wrapInKotlinTemplateClassContent(template, templateSourceContent)
        val kotlinFilePath = template.kotlinTemplateClassFilePath(templateConfiguration.templateBaseSrcPath)
        println("Writing file ${kotlinFilePath.absolutePathString()}")
        println("--------------------")
        println(kotlinTemplateClassContent)
        println("--------------------")
        kotlinFilePath.createParentDirectories()
        kotlinFilePath.writeText(kotlinTemplateClassContent)
    }
}
