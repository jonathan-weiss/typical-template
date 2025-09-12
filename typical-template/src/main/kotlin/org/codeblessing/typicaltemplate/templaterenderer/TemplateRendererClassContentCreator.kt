package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.contentparsing.commandchain.TemplateRendererDescription

object TemplateRendererClassContentCreator {

    private const val MULTILINE_STRING_DELIMITER = "\"\"\""

    fun wrapInKotlinClassContent(templateRendererDescription: TemplateRendererDescription, kotlinTemplateContent: KotlinTemplateContent): String {
        val sourceContent = kotlinTemplateContent.rendererCode
        val templateRendererPackageName = templateRendererDescription.templateRendererClass.classPackageName
        val templateRendererClassName = templateRendererDescription.templateRendererClass.className

        val templateRendererInterfaceClassName = templateRendererDescription.templateRendererInterface?.className
        val templateRendererInterfacePackageName = templateRendererDescription.templateRendererInterface?.classPackageName

        val extendsStatement = if(templateRendererInterfaceClassName != null) {
            ": $templateRendererInterfaceClassName "
        } else {
            ""
        }

        val templateRendererInterfaceFqnOrNull = if(templateRendererDescription.templateRendererInterface != null
            && templateRendererInterfacePackageName != templateRendererPackageName) {
            templateRendererInterfacePackageName
        } else {
            null
        }

        val modelImports = templateRendererDescription.modelClasses.map { it.modelClassDescription.fullQualifiedName }

        val allImports = listOfNotNull(
            templateRendererInterfaceFqnOrNull,
            *modelImports.toTypedArray(),
        ).distinct()
            .map { "import $it" }
            .joinToString("\n")


        val modelFields =
            templateRendererDescription.modelClasses
                .joinToString(", ") { "${it.modelName}: ${it.modelClassDescription.className}" }

        return """
/*
 * This file is generated using typical-template.
 */
package $templateRendererPackageName

$allImports

/**
 * Generate the content for the template $templateRendererClassName filled up
 * with the content of the passed models.
 */
object $templateRendererClassName $extendsStatement{

    fun renderTemplate(${modelFields}): String {
        return $MULTILINE_STRING_DELIMITER
${sourceContent.addIdentBeforeEachLine(ident = 10)}
        $MULTILINE_STRING_DELIMITER.trimMargin(marginPrefix = "|")
    }

    fun filePath(${modelFields}): String {
      return "${kotlinTemplateContent.filepath}"
    }
}
        """.trimIndent()
    }

    private fun String.addIdentBeforeEachLine(ident: Int): String {
        val identString = " ".repeat(ident)
        return this.lines()
            .joinToString("\n") { line -> identString + line }
    }

}
