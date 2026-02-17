package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.commandchain.CommandChainItem
import org.codeblessing.typicaltemplate.contentparsing.commandchain.TemplateRendererDescription

object TemplateRendererClassContentCreator {

    private const val MULTILINE_STRING_DELIMITER = "\"\"\""

    fun wrapInKotlinClassContent(templateRendererDescription: TemplateRendererDescription, kotlinTemplateContent: KotlinTemplateContent): String {
        val sourceContent = kotlinTemplateContent.rendererCode
        val templateRendererPackageName = templateRendererDescription.templateRendererClass.classPackageName
        val templateRendererClassName = templateRendererDescription.templateRendererClass.className

        val templateRendererInterfaceClassName = templateRendererDescription.templateRendererInterface?.className
        val templateRendererInterfacePackageName = templateRendererDescription.templateRendererInterface?.classPackageName

        val extendsStatement: String
        val overrideKeyword: String
        if(templateRendererInterfaceClassName != null) {
            extendsStatement = ": $templateRendererInterfaceClassName "
            overrideKeyword = "override "
        } else {
            extendsStatement = ""
            overrideKeyword = ""
        }

        val templateRendererInterfaceFqnOrNull = if(templateRendererDescription.templateRendererInterface != null
            && templateRendererInterfacePackageName != templateRendererPackageName) {
            templateRendererInterfacePackageName
        } else {
            null
        }

        val modelImports = templateRendererDescription.modelClasses.map { it.modelClassDescription.fullQualifiedName }

        val rendererImports = templateRendererDescription.templateChain
            .filterIsInstance<CommandChainItem>()
            .filter { it.keywordCommand.commandKey == CommandKey.RENDER_TEMPLATE }
            .mapNotNull {
                val className = it.keywordCommand.attribute(0, CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME)
                val packageName = it.keywordCommand.attributeOptional(0, CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME) ?: ""
                if (packageName.isNotBlank()) "$packageName.$className" else null
            }

        val allImports = listOfNotNull(
            templateRendererInterfaceFqnOrNull,
            *modelImports.toTypedArray(),
            *rendererImports.toTypedArray(),
        ).distinct().joinToString("\n") { "import $it" }


        val modelFields =
            templateRendererDescription.modelClasses
                .joinToString(", ") { modelDesc ->
                    val typeName = if (modelDesc.isList) {
                        "List<${modelDesc.modelClassDescription.className}>"
                    } else {
                        modelDesc.modelClassDescription.className
                    }
                    "${modelDesc.modelName}: $typeName"
                }

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

    ${overrideKeyword}fun renderTemplate(${modelFields}): String {
        return $MULTILINE_STRING_DELIMITER
${sourceContent.addIdentBeforeEachLine(ident = 10)}
        $MULTILINE_STRING_DELIMITER.trimMargin(marginPrefix = "|")
    }

    ${overrideKeyword}fun filePath(${modelFields}): String {
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
