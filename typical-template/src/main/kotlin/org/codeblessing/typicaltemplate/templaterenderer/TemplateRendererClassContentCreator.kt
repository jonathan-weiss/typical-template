package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.contentparsing.commandchain.TemplateRendererDescription

object TemplateRendererClassContentCreator {

    private const val MULTILINE_STRING_DELIMITER = "\"\"\""

    fun wrapInKotlinTemplateClassContent(templateRendererDescription: TemplateRendererDescription, sourceContent: String): String {
        val templateRendererPackageName = templateRendererDescription.templateRendererClass.classPackageName
        val templateRendererClassName = templateRendererDescription.templateRendererClass.className

        val modelImports =
            templateRendererDescription.modelClasses
                .joinToString("\n") { "import ${it.modelClassDescription.fullQualifiedName}\n" }

        val modelFields =
            templateRendererDescription.modelClasses
                .joinToString(", ") { "${it.modelName}: ${it.modelClassDescription.className}" }

        return """
/*
 * This file is generated using typical-template.
 */
package $templateRendererPackageName

$modelImports
/**
 * Generate the content for the template $templateRendererClassName filled up
 * with the content of the passed models.
 */
object $templateRendererClassName {

    fun renderTemplate(${modelFields}): String {
        return $MULTILINE_STRING_DELIMITER
${sourceContent.addIdentBeforeEachLine(ident = 10)}
        $MULTILINE_STRING_DELIMITER.trimMargin(marginPrefix = "|")
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
