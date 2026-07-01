package org.codeblessing.tavnit.templaterenderer

import org.codeblessing.tavnit.CommandAttributeKey
import org.codeblessing.tavnit.CommandKey
import org.codeblessing.tavnit.RelativeFile
import org.codeblessing.tavnit.contentparsing.commandchain.TemplateRendererDescription
import org.codeblessing.tavnit.contentparsing.resolver.TemplateCommentContentPart

object TemplateRendererClassContentCreator {

    private const val MULTILINE_STRING_DELIMITER = "\"\"\""

    fun wrapInKotlinClassContent(
        baseFile: RelativeFile,
        templateRendererDescription: TemplateRendererDescription,
        kotlinTemplateRendererMethodContent: KotlinTemplateRendererMethodContent
    ): String {
        val sourceContent = kotlinTemplateRendererMethodContent.rendererCode
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

        val templateRendererInterfaceFqnOrNull = if(
            templateRendererInterfaceClassName != null
            && templateRendererInterfacePackageName != null
            && templateRendererInterfacePackageName != templateRendererPackageName) {
            "${templateRendererInterfacePackageName}.${templateRendererInterfaceClassName}"
        } else {
            null
        }

        val modelImports = templateRendererDescription.modelClasses.map { it.modelClassDescription.fullQualifiedName }

        val rendererImports = templateRendererDescription.templateChain
            .filterIsInstance<TemplateCommentContentPart>()
            .flatMap { it.keywordCommands }
            .filter { it.commandKey == CommandKey.RENDER_TEMPLATE }
            .mapNotNull {
                val className = it.attribute(0, CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME)
                val packageName = it.attributeOptional(0, CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME) ?: ""
                if (packageName.isNotBlank()) "$packageName.$className" else null
            }

        val additionalImports = templateRendererDescription.templateChain
            .filterIsInstance<TemplateCommentContentPart>()
            .flatMap { it.keywordCommands }
            .filter { it.commandKey == CommandKey.ADD_IMPORT_TO_RENDERER }
            .flatMap { command ->
                command.attributeGroupIndices().map { groupIndex ->
                    val className = command.attribute(groupIndex, CommandAttributeKey.IMPORT_CLASS_NAME)
                    val packageName = command.attributeOptional(groupIndex, CommandAttributeKey.IMPORT_PACKAGE_NAME) ?: ""
                    if (packageName.isNotBlank()) "$packageName.$className" else className
                }
            }

        val allImports = listOfNotNull(
            templateRendererInterfaceFqnOrNull,
            *modelImports.toTypedArray(),
            *rendererImports.toTypedArray(),
            *additionalImports.toTypedArray(),
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
 * This file is generated using tavnit.
 */
package $templateRendererPackageName

$allImports

/**
 * Generate the content for the template `$templateRendererClassName`.
 *
 * This template renderer was generated from the template:
 * - file: `${baseFile.filePath.fileName}`
 * - path: `${baseFile.relativeToRootDirectory()}`
 */
object $templateRendererClassName $extendsStatement{

    ${overrideKeyword}fun renderTemplate(${modelFields}): String {
        return $MULTILINE_STRING_DELIMITER
${sourceContent.addIdentBeforeEachLine(ident = 10)}
        $MULTILINE_STRING_DELIMITER.trimMargin(marginPrefix = "|")
    }

    ${overrideKeyword}fun filePath(${modelFields}): String {
      return "${kotlinTemplateRendererMethodContent.filepath}"
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
