package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandAttributeKey.*
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TextContentPart


/**
 * Semantic-interpretation layer that transforms a validated list of parsed template content parts
 * into a structured [TemplateRendererDescription] — converting all fragments into a flat command
 * chain ready for code generation.
 *
 * Assumes input has already been validated by [KeywordCommandChainValidation].
 *
 * Responsibilities:
 * - **Entry point ([validateAndInterpretContentParts]):** Accepts a flat list of parsed template
 *   content parts and recursively processes nested template renderer definitions, returning one
 *   [TemplateRendererDescription] per renderer found.
 * - **Splits nested renderers:** Separates top-level fragments from nested
 *   `@template-renderer`...`@end-template-renderer` blocks, tracking depth to handle arbitrarily
 *   deep nesting.
 * - **Builds the chain:** Filters the remaining content parts into a flat list of [TemplateContentPart]s,
 *   omitting strip-line, template-renderer, and end-template-renderer commands from
 *   [TemplateCommentContentPart]s. Empty comment parts are dropped entirely.
 * - **Maps to descriptions:** Converts [KeywordCommand] attributes into [ClassDescription],
 *   [ModelDescription], and [TemplateRendererDescription] data objects for downstream code
 *   generation.
 */
object CommandChainCreator {

    private const val DEFAULT_PACKAGE_NAME = ""

    fun validateAndInterpretContentParts(templateContentParts: List<TemplateContentPart>): List<TemplateRendererDescription> {
        val (outerFragments, nestedSections) = splitNestedTemplateRenderers(templateContentParts)
        val result = mutableListOf<TemplateRendererDescription>()
        result.add(interpretSingleTemplate(outerFragments))
        for (nestedSection in nestedSections) {
            result.addAll(validateAndInterpretContentParts(nestedSection))
        }
        return result
    }

    private fun interpretSingleTemplate(templateContentParts: List<TemplateContentPart>): TemplateRendererDescription {
        val templateRendererKeywordCommand: KeywordCommand = findTemplateRendererCommand(templateContentParts)
        val templateModels = templateRendererKeywordCommand.toModelDescription()
        val templateRendererClassDescription = templateRendererKeywordCommand
            .toClassDescription(
                classNameAttribute = TEMPLATE_RENDERER_CLASS_NAME,
                packageNameAttribute = TEMPLATE_RENDERER_PACKAGE_NAME
            )

        val templateRendererInterfaceDescription = templateRendererKeywordCommand
            .toOptionalClassDescription(
                classNameAttribute = TEMPLATE_RENDERER_INTERFACE_NAME,
                packageNameAttribute = TEMPLATE_RENDERER_INTERFACE_PACKAGE_NAME
            )

        val templateChainItems = adaptMutualInfluencedFragments(templateContentParts)

        return TemplateRendererDescription(
            templateRendererClass = templateRendererClassDescription,
            templateRendererInterface = templateRendererInterfaceDescription,
            modelClasses = templateModels,
            templateChain = templateChainItems
        )
    }

    private data class SplitResult(
        val outerFragments: List<TemplateContentPart>,
        val nestedSections: List<List<TemplateContentPart>>,
    )

    /**
     * splitNestedTemplateRenderers walks the flat list of TemplateContentParts and partitions them into two groups:
     *   1. **outerFragments** — the content belonging to the top-level template renderer
     *      (everything before the first nested `@template-renderer`, plus the top-level `@template-renderer? command itself)
     *   2. **nestedSections** — a list of content-part lists, one per nested `@template-renderer`...`@end-template-renderer` block
     */
    private fun splitNestedTemplateRenderers(templateContentParts: List<TemplateContentPart>): SplitResult {
        val outerFragments = mutableListOf<TemplateContentPart>()
        val nestedSections = mutableListOf<List<TemplateContentPart>>()
        var foundTopLevel = false
        var depth = 0
        var currentNestedSection: MutableList<TemplateContentPart>? = null

        for (fragment in templateContentParts) {
            if (currentNestedSection != null) {
                if (fragment.isTemplateDefinitionCommand()) {
                    depth++
                    currentNestedSection.add(fragment)
                } else if (fragment.isEndTemplateRendererCommand()) {
                    if (depth > 0) {
                        depth--
                        currentNestedSection.add(fragment)
                    } else {
                        nestedSections.add(currentNestedSection)
                        currentNestedSection = null
                    }
                } else {
                    currentNestedSection.add(fragment)
                }
            } else {
                if (fragment.isTemplateDefinitionCommand()) {
                    if (!foundTopLevel) {
                        foundTopLevel = true
                        outerFragments.add(fragment)
                    } else {
                        currentNestedSection = mutableListOf(fragment)
                        depth = 0
                    }
                } else if (fragment.isEndTemplateRendererCommand()) {
                    outerFragments.add(fragment)
                    return SplitResult(outerFragments, nestedSections)
                } else {
                    outerFragments.add(fragment)
                }
            }
        }

        return SplitResult(outerFragments, nestedSections)
    }

    private fun adaptMutualInfluencedFragments(contentParts: List<TemplateContentPart>): List<TemplateContentPart> {
        val result = mutableListOf<TemplateContentPart>()
        contentParts.forEach { templateFragment ->
            when (templateFragment) {
                is TemplateCommentContentPart -> {
                    val filteredCommands = templateFragment.keywordCommands.filter { cmd ->
                        cmd.commandKey != CommandKey.STRIP_LINE_BEFORE_COMMENT &&
                        cmd.commandKey != CommandKey.STRIP_LINE_AFTER_COMMENT &&
                        cmd.commandKey != CommandKey.TEMPLATE_RENDERER &&
                        cmd.commandKey != CommandKey.END_TEMPLATE_RENDERER
                    }
                    if (filteredCommands.isNotEmpty()) {
                        result.add(templateFragment.copy(keywordCommands = filteredCommands))
                    }
                }
                is TextContentPart -> result.add(templateFragment)
            }
        }
        return result
    }

    private fun findTemplateRendererCommand(templateContentParts: List<TemplateContentPart>): KeywordCommand {
        return templateContentParts
            .filterIsInstance<TemplateCommentContentPart>()
            .first { it.isTemplateDefinitionCommand() }
            .keywordCommands.first { it.commandKey == CommandKey.TEMPLATE_RENDERER }
    }

    private fun KeywordCommand.toModelDescription(): List<ModelDescription> {
        return this.attributeGroupIndices().drop(1).map { attributeGroupIndex ->
            ModelDescription(
                modelClassDescription = this.toClassDescription(
                    groupId = attributeGroupIndex,
                    classNameAttribute = TEMPLATE_MODEL_CLASS_NAME,
                    packageNameAttribute = TEMPLATE_MODEL_PACKAGE_NAME,
                ),
                modelName = this.attribute(
                    groupId = attributeGroupIndex,
                    key = TEMPLATE_MODEL_NAME,
                ),
                isList = this.attributeOptional(
                    groupId = attributeGroupIndex,
                    key = TEMPLATE_MODEL_IS_LIST,
                )?.toBoolean() ?: false,
            )
        }
    }

    private fun KeywordCommand.toClassDescription(
        groupId: Int = 0,
        classNameAttribute: CommandAttributeKey,
        packageNameAttribute: CommandAttributeKey,
    ): ClassDescription {
        return ClassDescription(
            className = this.attribute(groupId, classNameAttribute),
            classPackageName = this.attributeOptional(groupId, packageNameAttribute) ?: DEFAULT_PACKAGE_NAME,
        )
    }

    private fun KeywordCommand.toOptionalClassDescription(
        groupId: Int = 0,
        classNameAttribute: CommandAttributeKey,
        packageNameAttribute: CommandAttributeKey,
    ): ClassDescription? {
        return this.attributeOptional(groupId, classNameAttribute)?.let { className ->
            ClassDescription(
                className = className,
                classPackageName = this.attributeOptional(groupId, packageNameAttribute) ?: DEFAULT_PACKAGE_NAME,
            )
        }
    }

    private fun TemplateContentPart.isTemplateDefinitionCommand(): Boolean {
        return this is TemplateCommentContentPart && this.keywordCommands.any { it.commandKey == CommandKey.TEMPLATE_RENDERER }
    }

    private fun TemplateContentPart.isEndTemplateRendererCommand(): Boolean {
        return this is TemplateCommentContentPart && this.keywordCommands.any { it.commandKey == CommandKey.END_TEMPLATE_RENDERER }
    }

}
