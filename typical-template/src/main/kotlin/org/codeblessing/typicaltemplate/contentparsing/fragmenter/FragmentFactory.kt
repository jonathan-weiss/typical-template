package org.codeblessing.typicaltemplate.contentparsing.fragmenter

import org.codeblessing.typicaltemplate.AttributeGroup
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.commentparser.StructuredComment
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

object FragmentFactory {

    fun createTextFragment(
        text: String,
        lineNumbers: LineNumbers
    ): TextFragment {
        return TextFragment(lineNumbers = lineNumbers, text = text)
    }

    fun createCommandFragment(
        structuredComment: StructuredComment,
        lineNumbers: LineNumbers
    ): CommandFragment {
        val keyword = structuredComment.keyword
        val commandKey = CommandKey.fromKeyword(keyword)

        if(commandKey == null) {
            throw TemplateParsingException(
                lineNumbers = lineNumbers,
                msg = "Invalid keyword '$keyword'.",
            )
        }

        val numberOfAttributeGroups = structuredComment.brackets.size
        if(numberOfAttributeGroups < commandKey.attributeGroupConstraint.minNumberOfAttributeGroups) {
            throw TemplateParsingException(
                lineNumbers = lineNumbers,
                msg = "Invalid number of attributes groups. " +
                        "Must be at least ${commandKey.attributeGroupConstraint.minNumberOfAttributeGroups} but was ${numberOfAttributeGroups}.",
            )
        }

        if(numberOfAttributeGroups > commandKey.attributeGroupConstraint.maxNumberOfAttributeGroups) {
            throw TemplateParsingException(
                lineNumbers = lineNumbers,
                msg = "Invalid number of attributes groups. " +
                        "Only ${commandKey.attributeGroupConstraint.maxNumberOfAttributeGroups} are allowed but was ${numberOfAttributeGroups}.",
            )
        }

        val attributeGroups = structuredComment.brackets.mapIndexed { groupIndex, attributeMap ->
            AttributeGroup(
                attributes = attributeMap
                    .mapKeys { (keyString) ->
                        CommandAttributeKey.Companion.fromString(keyString)
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
                        if (attributeKey.allowedValues != null && attributeValue !in attributeKey.allowedValues) {
                            throw TemplateParsingException(
                                lineNumbers = lineNumbers,
                                msg = "Not allowed attribute value '$attributeValue' for " +
                                        "key '${attributeKey.keyAsString}' in attributes group #${groupIndex + 1}. " +
                                        "Only the following attributes are allowed: ${attributeKey.allowedValues}.",
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
                }
            }



        val keywordCommand = KeywordCommand(commandKey, attributeGroups)
        return CommandFragment(lineNumbers = lineNumbers, keywordCommand = keywordCommand)
    }
}
