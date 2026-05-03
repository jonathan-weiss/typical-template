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
 * - **Builds the chain:** Converts the remaining content parts into a flat list of [ChainItem]s —
 *   either [CommandChainItem] (for keyword commands) or [PlainTextChainItem] (for raw text).
 *   Strip-line commands are not added as chain items themselves but instead influence adjacent
 *   [PlainTextChainItem]s to trim leading/trailing whitespace-only lines.
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

    private fun adaptMutualInfluencedFragments(contentParts: List<TemplateContentPart>): List<ChainItem> {
        val templateChainItems = mutableListOf<ChainItem>()
        contentParts.forEachIndexed { index, templateFragment ->
            when (templateFragment) {
                is TemplateCommentContentPart -> {
                    templateFragment.keywordCommands.forEach { keywordCommand ->
                        when (keywordCommand.commandKey) {
                            CommandKey.STRIP_LINE_BEFORE_COMMENT,
                            CommandKey.STRIP_LINE_AFTER_COMMENT,
                            CommandKey.TEMPLATE_RENDERER,
                            CommandKey.END_TEMPLATE_RENDERER -> Unit
                            else -> templateChainItems.add(CommandChainItem(keywordCommand = keywordCommand))
                        }
                    }
                }
                is TextContentPart -> {
                    templateChainItems.add(createPlainTextChainItem(templateFragment, index, contentParts))
                }
            }
        }
        return templateChainItems
    }

    private fun createPlainTextChainItem(
        templateFragment: TextContentPart,
        index: Int,
        contentParts: List<TemplateContentPart>
    ): PlainTextChainItem {
        val hasPrecedingStripLineAfterComment = contentParts
            .subList(0, index)
            .reversed()
            .hasAnyFollowingCommandBeforeNextText(CommandKey.STRIP_LINE_AFTER_COMMENT)

        val hasFollowingStripLineBeforeComment = contentParts
            .subList(index + 1, contentParts.size)
            .hasAnyFollowingCommandBeforeNextText(CommandKey.STRIP_LINE_BEFORE_COMMENT)

        return PlainTextChainItem(
            text = templateFragment.text,
            removeFirstLineIfWhitespaces = hasPrecedingStripLineAfterComment,
            removeLastLineIfWhitespaces = hasFollowingStripLineBeforeComment,
        )
    }

    private fun List<TemplateContentPart>.hasAnyFollowingCommandBeforeNextText(commandKey: CommandKey): Boolean {
        return this.takeWhile { it !is TextContentPart }
            .filterIsInstance<TemplateCommentContentPart>()
            .any { part -> part.keywordCommands.any { it.commandKey == commandKey } }
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
