package org.codeblessing.typicaltemplate.contentparsing.resolver

import org.codeblessing.typicaltemplate.AttributeGroup
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.commentparser.CommandStructure
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

/**
 * Translates a raw [CommandStructure] (parsed comment text) into a fully validated [KeywordCommand],
 * rejecting invalid input with a [TemplateParsingException].
 * It does so by:
 * - Resolving the keyword string to a known [CommandKey], failing fast on unknown keywords
 * - Checking that the number of bracket groups falls within the min/max bounds derived from the command's [CommandKey.attributeGroupConstraints]
 * - Converting each raw attribute key string to a typed [CommandAttributeKey], rejecting strings not present in the enum
 * - Verifying each attribute key is permitted for its specific group index (groups beyond the constraint list reuse the last constraint)
 * - Validating each attribute value against the key's allowed values list when one is defined
 * - Rejecting blank attribute values for keys that require a non-empty value
 * - Ensuring all required attributes for each group are present
 * - Ensuring no attributes outside the allowed set for each group have snuck through
 */
object KeywordCommandFactory {

    fun createKeywordCommand(
        commandStructure: CommandStructure,
        lineNumbers: LineNumbers
    ): KeywordCommand {
        val keyword = commandStructure.keyword
        val commandKey = CommandKey.fromKeyword(keyword) ?: throw TemplateParsingException(
            lineNumbers = lineNumbers,
            msg = "Invalid keyword '$keyword'.",
        )

        val numberOfAttributeGroups = commandStructure.brackets.size
        if (numberOfAttributeGroups < commandKey.minNumberOfAttributeGroups) {
            throw TemplateParsingException(
                lineNumbers = lineNumbers,
                msg = "Invalid number of attributes groups. " +
                        "Must be at least ${commandKey.minNumberOfAttributeGroups} but was ${numberOfAttributeGroups}.",
            )
        }

        if (numberOfAttributeGroups > commandKey.maxNumberOfAttributeGroups) {
            throw TemplateParsingException(
                lineNumbers = lineNumbers,
                msg = "Invalid number of attributes groups. " +
                        "Only ${commandKey.maxNumberOfAttributeGroups} are allowed but was ${numberOfAttributeGroups}.",
            )
        }

        val attributeGroups = commandStructure.brackets.mapIndexed { groupIndex, attributeMap ->
            AttributeGroup(
                attributes = attributeMap
                    .mapKeys { (keyString) ->
                        CommandAttributeKey.fromString(keyString)
                            ?: throw TemplateParsingException(
                                lineNumbers = lineNumbers,
                                msg = "Unknown attribute key '$keyString' in attributes group #${groupIndex + 1}. " +
                                        "Only the following attributes are allowed: ${commandKey.allowedAttributesForGroup(groupIndex).map { it.keyAsString }}.",
                            )
                    }
                    .onEach { (attributeKey, attributeValue) ->
                        if (attributeKey !in commandKey.allowedAttributesForGroup(groupIndex)) {
                            throw TemplateParsingException(
                                lineNumbers = lineNumbers,
                                msg = "Not allowed attribute key '${attributeKey.keyAsString}' " +
                                        "in attributes group #${groupIndex + 1}. " +
                                        "Only the following attributes are " +
                                        "allowed: ${commandKey.allowedAttributesForGroup(groupIndex).map { it.keyAsString }}.",
                            )
                        }
                        if (attributeKey.allowedValues != null && attributeValue !in attributeKey.allowedValues.map { it.value }) {
                            throw TemplateParsingException(
                                lineNumbers = lineNumbers,
                                msg = "Not allowed attribute value '$attributeValue' for " +
                                        "key '${attributeKey.keyAsString}' in attributes group #${groupIndex + 1}. " +
                                        "Only the following attributes are allowed: ${attributeKey.allowedValues.map { it.value }}.",
                            )
                        }
                        if (attributeKey.requireNotEmpty && attributeValue.isBlank()) {
                            throw TemplateParsingException(
                                lineNumbers = lineNumbers,
                                msg = "The attribute value for key '${attributeKey.keyAsString}' " +
                                        "in attributes group #${groupIndex + 1} must not be blank."
                            )
                        }

                    }
            ).also { attributeGroup ->
                    val missingAttributes = commandKey.missingRequiredAttributesForGroup(groupIndex, attributeGroup.attributes.keys)
                    if(missingAttributes.isNotEmpty()) {
                        throw TemplateParsingException(
                            lineNumbers = lineNumbers,
                            msg = "Not all required attributes are present for command '${commandKey.keyword}'. " +
                                    "The following attributes are missing in attributes group #${groupIndex + 1}: " +
                                    "${missingAttributes.joinToString { it.keyAsString }} ",
                        )
                    }
                    val unallowedAttributes = commandKey.unallowedAttributesForGroup(groupIndex, attributeGroup.attributes.keys)
                    if(unallowedAttributes.isNotEmpty()) {
                        throw TemplateParsingException(
                            lineNumbers = lineNumbers,
                            msg = "The following attributes are not allowed for " +
                                    "command '${commandKey.keyword}' in attributes group #${groupIndex + 1}: " +
                                    "${unallowedAttributes.joinToString { it.keyAsString }} ",
                        )
                    }
                    val mutualExclusiveAttributes = commandKey.mutualExclusiveAttributesForGroup(groupIndex)
                    val presentMutualExclusiveAttributes = attributeGroup.attributes.keys.intersect(mutualExclusiveAttributes)
                    if (presentMutualExclusiveAttributes.size > 1) {
                        throw TemplateParsingException(
                            lineNumbers = lineNumbers,
                            msg = "Only one of the following mutually exclusive attributes may be present for " +
                                    "command '${commandKey.keyword}' in attributes group #${groupIndex + 1}: " +
                                    "${mutualExclusiveAttributes.joinToString { it.keyAsString }}. " +
                                    "Found: ${presentMutualExclusiveAttributes.joinToString { it.keyAsString }}.",
                        )
                    }
                }
            }

        return KeywordCommand(commandKey, attributeGroups)
    }
}
