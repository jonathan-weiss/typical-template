package org.codeblessing.typicaltemplate.template

import org.codeblessing.typicaltemplate.contentparsing.Template

object TemplateClassContentCreator {

    private const val MULTILINE_STRING_DELIMITER = "\"\"\""

    fun wrapInKotlinTemplateClassContent(template: Template, sourceContent: String): String {
        val templatePackageName = template.kotlinTemplatePackage()
        val templateClassName = template.kotlinTemplateClassname()

        val modelClassName = template.kotlinModelClassname()
        val modelFqn = template.kotlinModelFullQualifiedName()

        return """
/*
 * This file is generated using typical-template.
 */
package $templatePackageName

import $modelFqn

/**
 * Generate the content for the template $templateClassName filled up
 * with the content of the model [$modelFqn].
 */
object class $templateClassName {

    fun renderTemplate(model: ${modelClassName}): String {
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
