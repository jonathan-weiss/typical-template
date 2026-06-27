package org.codeblessing.typicaltemplate.documentation

import org.codeblessing.typicaltemplate.AttributeGroupOccurrence
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.IsListValue

object CommandReferenceMarkdownCreator {

    private const val COMMAND_PREFIX = "@"

    // Shared note appended to every remove-blanks command, explaining the default whitespace
    // handling these commands opt out of.
    private val DEFAULT_WHITESPACE_HANDLING_NOTE =
        "Note on the default behaviour: a comment that stands alone on its line (only blanks before it on its line and " +
                "only blanks after it up to the line break) has its surrounding whitespace collapsed automatically: the " +
                "blanks before the comment as well as the blanks and the line break after the comment are removed. As soon as " +
                "any of the remove-blanks commands is present on a comment, this default handling is switched off for the whole " +
                "comment and only the explicitly requested removals are applied (e.g. using only " +
                "```${COMMAND_PREFIX}${CommandKey.REMOVE_BLANKS_BEFORE_COMMENT.keyword}``` on an otherwise stand-alone comment " +
                "therefore keeps the line break after it). The keep-blanks commands, in contrast, do not switch off the default " +
                "handling; they only suppress it on their side."

    // linked map to preserve the order of the keys
    private val commandKeyDocumentation: Map<CommandKey, List<String>> = linkedMapOf(
        CommandKey.TEMPLATE_RENDERER to listOf(
            "Defines the template renderer kotlin class in which the content of the given file is put into. Optionally declares model instances (kotlin function parameters) passed to the renderer. ",
            "The first attribute group specifies the renderer class; subsequent repeating groups each define one model parameter.",
            "Additional ${CommandKey.TEMPLATE_RENDERER.keyword} commands can be nested inside the top-level one; each nested template-renderer produces an independent renderer class and is closed with ${CommandKey.END_TEMPLATE_RENDERER.keyword}. " +
                "A nested template renderer is completely independent (and its content therefore removed from) the parent template renderer. " +
                "Also all other commands defined in the parent template (models, if..else, replacements, etc.) will not affect the child template renderer, as each template renderer resides in its own class.",
        ),
        CommandKey.END_TEMPLATE_RENDERER to emptyList(),
        CommandKey.REPLACE_VALUE_BY_EXPRESSION to listOf(
            "Replaces a value by a kotlin expression in a multiline string. The expression is often accessing properties or functions on a model instance declared with the ${CommandKey.TEMPLATE_RENDERER.keyword} command.",
        ),
        CommandKey.END_REPLACE_VALUE_BY_EXPRESSION to emptyList(),
        CommandKey.REPLACE_VALUE_BY_VALUE to listOf(
            "Replaces a value by another (fixed) value.",
        ),
        CommandKey.END_REPLACE_VALUE_BY_VALUE to emptyList(),
        CommandKey.IF_CONDITION to listOf(
            "Render the enclosed content only if the condition expression evaluates to true.",
        ),
        CommandKey.ELSE_IF_CONDITION to listOf(
            "Render the enclosed content only if the condition expression evaluates to true and all previous conditions of the ${CommandKey.IF_CONDITION.keyword}/${CommandKey.ELSE_IF_CONDITION.keyword} conditions evaluates to false.",
        ),
        CommandKey.ELSE_CLAUSE to listOf(
            "Render the enclosed content only if all previous ${CommandKey.IF_CONDITION.keyword}/${CommandKey.ELSE_IF_CONDITION.keyword} conditions evaluates to false",
        ),
        CommandKey.END_IF_CONDITION to emptyList(),
        CommandKey.FOREACH to listOf(
            "Iterates/Loops over a collection of items (=iterable). In each loop, the current item is hold in a loop variable.",
        ),
        CommandKey.END_FOREACH to emptyList(),
        CommandKey.IGNORE_TEXT to listOf(
            "Ignores the text and does not output it in the template renderer.",
        ),
        CommandKey.END_IGNORE_TEXT to emptyList(),
        CommandKey.PRINT_TEXT to listOf(
            "Print additional text as output of the template renderer.",
        ),
        CommandKey.MODIFY_PROVIDED_FILENAME_BY_REPLACEMENTS to listOf(
            "Each template renderer provides the path of the source file as string. By using this command, the path can be modified with all replacements " +
                "provided by ```${CommandKey.REPLACE_VALUE_BY_EXPRESSION.keyword}``` and ```${CommandKey.REPLACE_VALUE_BY_VALUE.keyword}``` the " +
                "```${CommandKey.MODIFY_PROVIDED_FILENAME_BY_REPLACEMENTS.keyword}``` command is currently nested in.",
            "The intention of this command is that the filename and path can also take part of the replacements and this has not to be handled " +
                    "separately and outside of the template renderer; the replacements for the filename follow often the same patterns as for the file content. " +
                    "If you change in your template every ```foo``` to ```bar```, it is likely that you also want to change the path of the file " +
                    "e.g. from ```src/foo/foo.txt``` to ```src/bar/bar.txt``` to generate dynamic file paths.",
            "You can use this command multiple times per template renderer. The replacements are done one after another in the order of the command usage.",
            "If you create multiple template renderers from one file (multiple ${CommandKey.TEMPLATE_RENDERER.keyword}), you can (and have to) " +
                    "call ```${CommandKey.MODIFY_PROVIDED_FILENAME_BY_REPLACEMENTS.keyword}``` for each template renderer individually.",
        ),
        CommandKey.RENDER_TEMPLATE to listOf(
            "Calls another template renderer and embeds its output. The first attribute group specifies the renderer class; subsequent groups map model parameters to expressions.",
            "This command's syntax has a lot of similarity to ${CommandKey.TEMPLATE_RENDERER.keyword}, as it calls a " +
                    "template renderer defined by the ${CommandKey.TEMPLATE_RENDERER.keyword} block."
        ),
        CommandKey.MOVE_COMMENT_BACKWARD to listOf(
            "Moves the whole comment in which this command is written backward (i.e. before the preceding text). " +
                    "Optionally positions it relative to the first or last occurrence of a given text in the surrounding content. " +
                    "The comment will be moved at most to the previous comment or to the beginning of the file.",
            "This is useful as some file formats do not allow to put a comment as first line of the file.",
            "Example:  XML starts with a preamble like ```<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>``` and this " +
                    "text should be part of the template renderer's output. But it is not possible to write a XML comment before " +
                    "this preamble. To still span the template from the beginning of the file, you can move the comment to the " +
                    "beginning of the file using this command " +
                    "(```${CommandKey.MOVE_COMMENT_BACKWARD.commandPrefix()}${CommandKey.MOVE_COMMENT_BACKWARD.keyword}```)",
        ),
        CommandKey.MOVE_COMMENT_FORWARD to listOf(
            "Moves the whole comment in which this command is written forward (i.e. after the following text). " +
                    "Optionally positions it relative to the first or last occurrence of a given text in the surrounding content. " +
                    "The comment will be moved at most to the next comment or to the end of the file.",
        ),
        CommandKey.REMOVE_BLANKS_BEFORE_COMMENT to listOf(
            "Removes the consecutive blanks (spaces and tabs) directly preceding the comment from the neighboring text part. " +
                    "Stops before the line-ending; the line-ending itself is kept.",
            "This is useful if you don't want to have dangling spaces/indents in your template output if the " +
                    "typical template comments itself have to follow some indentation rules (e.g. by your linter).",
            DEFAULT_WHITESPACE_HANDLING_NOTE,
        ),
        CommandKey.REMOVE_BLANKS_AFTER_COMMENT to listOf(
            "Removes the consecutive blanks (spaces and tabs) directly following the comment from the neighboring text part. " +
                    "Stops before the line-ending; the line-ending itself is kept.",
            "This is useful if you don't want to have dangling spaces/indents in your template output if the " +
                    "typical template comments itself have to follow some indentation rules (e.g. by your linter).",
            DEFAULT_WHITESPACE_HANDLING_NOTE,
        ),
        CommandKey.REMOVE_BLANKS_AND_LINEBREAK_BEFORE_COMMENT to listOf(
            "Removes the consecutive blanks (spaces and tabs) directly preceding the comment from the neighboring text part, " +
                    "including the immediately adjacent line-ending.",
            "This is useful if you don't want to have empty lines in your template output due to the typical templates comments.",
            DEFAULT_WHITESPACE_HANDLING_NOTE,
        ),
        CommandKey.REMOVE_BLANKS_AND_LINEBREAK_AFTER_COMMENT to listOf(
            "Removes the consecutive blanks (spaces and tabs) directly following the comment from the neighboring text part, " +
                    "including the immediately adjacent line-ending.",
            "This is useful if you don't want to have empty lines in your template output due to the typical templates comments.",
            DEFAULT_WHITESPACE_HANDLING_NOTE,
        ),
        CommandKey.KEEP_BLANKS_AND_LINEBREAK_BEFORE_COMMENT to listOf(
            "Keeps the consecutive blanks (spaces and tabs) directly preceding the comment, i.e. it suppresses " +
                    "the default whitespace handling that would otherwise remove the blanks before a comment that stands alone on its line. " +
                    "Note that the default handling never removes the line-ending before the comment (it belongs to the preceding line), " +
                    "so on the before side this command only affects the blanks.",
            "This is the counterpart of ```${CommandKey.REMOVE_BLANKS_AND_LINEBREAK_BEFORE_COMMENT.commandPrefix()}${CommandKey.REMOVE_BLANKS_AND_LINEBREAK_BEFORE_COMMENT.keyword}```: " +
                    "use it when you want to keep the whitespace before the comment that would otherwise be collapsed.",
        ),
        CommandKey.KEEP_BLANKS_AND_LINEBREAK_AFTER_COMMENT to listOf(
            "Keeps the consecutive blanks (spaces and tabs) and the line-ending directly following the comment, i.e. it suppresses " +
                    "the default whitespace handling that would otherwise remove the blanks and the line break after a comment that stands alone on its line.",
            "This is the counterpart of ```${CommandKey.REMOVE_BLANKS_AND_LINEBREAK_AFTER_COMMENT.commandPrefix()}${CommandKey.REMOVE_BLANKS_AND_LINEBREAK_AFTER_COMMENT.keyword}```: " +
                    "use it when you want to keep the whitespace after the comment that would otherwise be collapsed.",
        ),
    )


    // linked map to preserve the order of the keys
    private val commandAttributeKeyDocumentation: Map<CommandAttributeKey, List<String>> = linkedMapOf(
        CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME to listOf(
            "The name of the template class that will generate this template.",
        ),
        CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME to listOf(
            "The name of the package where the class defined with ```${CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME.keyAsString}``` resides in.",
        ),
        CommandAttributeKey.TEMPLATE_RENDERER_INTERFACE_NAME to listOf(
            "The name of an optional interface class name that is added to the class defined with the ```${CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME.keyAsString}```.",
        ),
        CommandAttributeKey.TEMPLATE_RENDERER_INTERFACE_PACKAGE_NAME to listOf(
            "The name of the package where the interface defined with ```${CommandAttributeKey.TEMPLATE_RENDERER_INTERFACE_NAME.keyAsString}``` resides in.",
        ),
        CommandAttributeKey.TEMPLATE_MODEL_NAME to listOf(
            "The name of the model variable. The variable can later be used to access fields and functions on the model e.g. in conditions or as replacement values.",
        ),
        CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME to listOf(
            "The name of the model class. This class provides all the fields in the template.",
        ),
        CommandAttributeKey.TEMPLATE_MODEL_PACKAGE_NAME to listOf(
            "The name of the package where the model class defined with ```${CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME.keyAsString}``` resides in.",
        ),
        CommandAttributeKey.TEMPLATE_MODEL_IS_LIST to listOf(
            "When set to ```${IsListValue.YES.value}```, the model parameter is declared as a list of the model class defined with ```${CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME.keyAsString}```, i.e. ```List<ModelClass>``` instead of ```ModelClass```. Defaults to ```${IsListValue.NO.value}```.",
        ),
        CommandAttributeKey.SEARCH_VALUE to listOf(
            "The token that has to be searched in the enclosed block of content. The search is case-sensitive.",
        ),
        CommandAttributeKey.REPLACE_BY_EXPRESSION to listOf(
            "The expression accessing the model class with which the token defined with the attribute ```${CommandAttributeKey.SEARCH_VALUE.keyAsString}``` is replaced.",
        ),
        CommandAttributeKey.REPLACE_BY_VALUE to listOf(
            "The plain value the attribute ```${CommandAttributeKey.SEARCH_VALUE.keyAsString}``` is replaced.",
        ),
        CommandAttributeKey.CONDITION_EXPRESSION to listOf(
            "The condition returning a boolean value that is used for the if statement or else-if statement.",
        ),
        CommandAttributeKey.LOOP_ITERABLE_EXPRESSION to listOf(
            "The expression returning the collection/iterable that is looped over.",
        ),
        CommandAttributeKey.LOOP_VARIABLE_NAME to listOf(
            "The name of the loop variable, similar to the model variable from ```${CommandAttributeKey.TEMPLATE_MODEL_NAME.keyAsString}```. The variable holds the current instance of the loop iterable defined with ```${CommandAttributeKey.LOOP_ITERABLE_EXPRESSION.keyAsString}```.",
        ),
        CommandAttributeKey.TEXT to listOf(
            "Text that is to print as-is into the template renderer.",
        ),
        CommandAttributeKey.MODEL_EXPRESSION to listOf(
            "The expression that provides the value for the model parameter specified by ```${CommandAttributeKey.TEMPLATE_MODEL_NAME.keyAsString}``` when calling the template renderer.",
        ),
        CommandAttributeKey.BEFORE_FIRST_OCCURRENCE_OF to listOf(
            "Positions the comment before the first occurrence of the given text in the surrounding content.",
        ),
        CommandAttributeKey.AFTER_FIRST_OCCURRENCE_OF to listOf(
            "Positions the comment after the first occurrence of the given text in the surrounding content.",
        ),
        CommandAttributeKey.BEFORE_LAST_OCCURRENCE_OF to listOf(
            "Positions the comment before the last occurrence of the given text in the surrounding content.",
        ),
        CommandAttributeKey.AFTER_LAST_OCCURRENCE_OF to listOf(
            "Positions the comment after the last occurrence of the given text in the surrounding content.",
        ),
    )

    private fun CommandKey.createMarkDownChapterLink(): String {
        return "[$keyword](#${keyword.lowercase().replace(' ', '-')})"
    }

    fun createMarkdownDocumentation(): String {
        val sb = StringBuilder()
        sb.appendLine("""
            # Keyword/Command reference
        """.trimIndent())

        sb.appendLine("""

            The following keywords/commands are supported:
        """.trimIndent())
        for ((commandKey, _) in commandKeyDocumentation) {
            val aliases = if(commandKey.aliases.isNotEmpty()) " (${commandKey.aliases.joinToString()})" else ""
            sb.appendLine("* ${commandKey.createMarkDownChapterLink()}${aliases}")
        }
        sb.appendLine("""

            Commands always start with a `$COMMAND_PREFIX`.


        """.trimIndent())

        for ((commandKey, docLines) in commandKeyDocumentation) {
            sb.appendLine("""

                    ## ${commandKey.keyword}

                    Syntax: ```${commandKey.commandSyntax()}```
                """.trimIndent()
            )
            sb.appendLine("""

                    ${commandKey.aliasesDescription()}
                """.trimIndent()
            )
            for (line in docLines) {
                sb.appendLine("""

                        $line
                    """.trimIndent()
                )
            }
            sb.appendLine("""

                    Varia:
                    * ${commandKey.openingClosingDescription()}
                    * ${commandKey.autoclosingDescription()}
                    * ${commandKey.groupSupportDescription()}
                    * ${commandKey.nestingDescription()}
                """.trimIndent()
            )
            for ((constraintIndex, constraint) in commandKey.attributeGroupConstraints.withIndex()) {
                val sectionLabel = when {
                    commandKey.attributeGroupConstraints.size > 1 && constraintIndex == 0 -> "Primary Attributes"
                    constraint.occurrence == AttributeGroupOccurrence.MANY_ATTRIBUTE_GROUP
                            || constraint.occurrence == AttributeGroupOccurrence.ZERO_OR_MANY_ATTRIBUTE_GROUP -> "Repeatable Group Attributes"
                    else -> "Attributes"
                }
                sb.appendLine("""

                        $sectionLabel:
                    """.trimIndent()
                )
                val attributesDocumentation = commandAttributeKeyDocumentation.filter { it.key in constraint.allowedAttributes }
                createAttributeDocumentation(attributesDocumentation, constraint.requiredAttributes, constraint.mutualExclusiveAttributes, sb)
            }
        }
        return sb.toString()
    }

    private fun createAttributeDocumentation(
        attributesDocumentation: Map<CommandAttributeKey, List<String>>,
        requiredAttributes: Set<CommandAttributeKey>,
        mutualExclusiveAttributes: Set<CommandAttributeKey>,
        sb: StringBuilder
    ) {
        for((attributeKey, attributeDocLines) in attributesDocumentation) {
            val mutualExclusiveWith = calculateMutualExclusiveAttributes(attributeKey, mutualExclusiveAttributes)
            val descriptionText = attributeDocLines.joinToString("\n\n  ")
            sb.appendLine("""
                    * *${attributeKey.keyAsString}*: $descriptionText
                      * Required attribute: _${(attributeKey in requiredAttributes).yesOrNo()}_
                      * Required not empty: _${attributeKey.requireNotEmpty.yesOrNo()}_
                      * Allowed values: ${if(attributeKey.allowedValues == null) "_\\<unrestricted\\>_" else attributeKey.allowedValues.joinToString(",") { "```${it.value}```"}}
                      * Mutually exclusive with: ${if(mutualExclusiveWith.isEmpty()) "none" else mutualExclusiveWith.joinToString(", ") { "```${it.keyAsString}```" }}
                    """.trimIndent()
            )
        }
    }

    private fun calculateMutualExclusiveAttributes(
        attribute: CommandAttributeKey,
        mutualExclusiveAttributes: Set<CommandAttributeKey>
    ): Set<CommandAttributeKey> {
        if(attribute !in mutualExclusiveAttributes) {
            return emptySet()
        }
        return mutualExclusiveAttributes - attribute
    }

    private fun CommandKey.commandSyntax(): String {
        val sb = StringBuilder()
        sb.append("${commandPrefix()}${keyword}")
        if (!isClosingCommand) {
            for (constraint in attributeGroupConstraints) {
                sb.append(" [ ")
                constraint.allowedAttributes.forEach { attribute ->
                    val attributeValue = if (attribute.allowedValues == null) "..." else attribute.allowedValues.joinToString("|") { it.value }
                    sb.append("${attribute.keyAsString}=\"${attributeValue}\" ")
                }
                sb.append("]")
                if (constraint.occurrence == AttributeGroupOccurrence.MANY_ATTRIBUTE_GROUP
                    || constraint.occurrence == AttributeGroupOccurrence.ZERO_OR_MANY_ATTRIBUTE_GROUP) {
                    sb.append(" [ ... ]")
                }
            }
            correspondingClosingCommandKey?.let { closingKey ->
                sb.append(" .... ${closingKey.commandPrefix()}${closingKey.keyword}")
            }
        }
        return sb.toString()
    }

    private fun CommandKey.openingClosingDescription(): String {
        return if(!isOpeningCommand && !isClosingCommand) {
            "This command stands for itself and does not need to be closed by another command."
        } else if(isOpeningCommand) {
            "This command must be closed using the ${correspondingClosingCommandKey!!.createMarkDownChapterLink()} command."
        } else {
            "This command is closing the ${correspondingOpeningCommandKey!!.createMarkDownChapterLink()} command."
        }
    }

    private fun CommandKey.autoclosingDescription(): String {
        val correspondingClosingCommand = this.correspondingClosingCommandKey
        return if(isTriggerAutoclose) {
            "This command triggers to close all nested commands that support auto-closing."
        } else if(isAutoclosingSupported) {
            "This command supports to be auto-closed. The corresponding ${correspondingClosingCommand!!.createMarkDownChapterLink()} command can be skipped."
        } else {
            "This command neither triggers an auto-closing of nested commands nor will it be auto-closed."
        }
    }

    private fun CommandKey.groupSupportDescription(): String =
        when {
            attributeGroupConstraints.isEmpty() ->
                "This command/keyword does not support groups and has no attributes."
            attributeGroupConstraints.size == 1 && attributeGroupConstraints[0].occurrence == AttributeGroupOccurrence.ONE_ATTRIBUTE_GROUP ->
                "This command/keyword must have exactly one group of attributes."
            attributeGroupConstraints.size == 1 && attributeGroupConstraints[0].occurrence == AttributeGroupOccurrence.ZERO_OR_ONE_ATTRIBUTE_GROUP ->
                "This command can have zero or one group of attributes."
            attributeGroupConstraints.size == 1 && attributeGroupConstraints[0].occurrence == AttributeGroupOccurrence.MANY_ATTRIBUTE_GROUP ->
                "This command can have many groups of attributes"
            attributeGroupConstraints.size == 1 && attributeGroupConstraints[0].occurrence == AttributeGroupOccurrence.ZERO_OR_MANY_ATTRIBUTE_GROUP ->
                "This command can have zero or many groups of attributes"
            attributeGroupConstraints.size > 1 && attributeGroupConstraints.last().occurrence == AttributeGroupOccurrence.MANY_ATTRIBUTE_GROUP ->
                "This command has a primary group of attributes followed by one or more groups of attributes."
            attributeGroupConstraints.size > 1 && attributeGroupConstraints.last().occurrence == AttributeGroupOccurrence.ZERO_OR_MANY_ATTRIBUTE_GROUP ->
                "This command has a primary group of attributes optionally followed by zero or more groups of attributes."
            attributeGroupConstraints.size > 1 && attributeGroupConstraints.last().occurrence == AttributeGroupOccurrence.ZERO_OR_ONE_ATTRIBUTE_GROUP ->
                "This command has a primary group of attributes optionally followed by one more group of attributes."
            else ->
                "This command/keyword has ${attributeGroupConstraints.size} fixed groups of attributes."
        }

    private fun CommandKey.aliasesDescription(): String {
        if (aliases.isEmpty()) {
            return "Aliases: _none_"
        }
        val aliasesAsMarkdown = aliases.joinToString(", ") { "```${commandPrefix()}$it```" }
        return "Aliases: $aliasesAsMarkdown (can be used in place of ```${commandPrefix()}${keyword}```)"
    }

    private fun CommandKey.nestingDescription(): String =
        if(directlyNestedInsideCommandKey == null) {
            "This command/keyword is NOT forced to reside as nested element in a certain parent element."
        } else {
            "This command/keyword must reside as directly nested element in the parent element ${directlyNestedInsideCommandKey.createMarkDownChapterLink()}."
        }

    private fun Boolean.yesOrNo(): String {
        return if (this) "Yes" else "No"
    }

    private fun CommandKey.commandPrefix(): String = COMMAND_PREFIX
}
