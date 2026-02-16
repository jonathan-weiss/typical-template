package org.codeblessing.typicaltemplate.documentation

import org.codeblessing.typicaltemplate.AttributeGroupConstraint
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey

object MarkdownCreator {

    private const val COMMAND_PREFIX = "@"

    // linked map to preserve the order of the keys
    private val commandKeyDocumentation: Map<CommandKey, String> = linkedMapOf(
        CommandKey.TEMPLATE_RENDERER to "Defines in which template the content of the given file is put into. This command must be the first command and can only occur one time per file. Additional template-renderer commands can be nested inside the top-level one; each nested template-renderer produces an independent renderer class and must be closed with end-template-renderer.",
        CommandKey.END_TEMPLATE_RENDERER to "Closes a nested template-renderer block. Required for nested template-renderers; optional for the top-level template-renderer.",
        CommandKey.TEMPLATE_MODEL to "Defines model instances that are passed to the template renderer. You can access these instances in your template render to fill data into your template.",
        CommandKey.REPLACE_VALUE_BY_EXPRESSION to "Replaces a value by a kotlin expression in a multiline string.",
        CommandKey.END_REPLACE_VALUE_BY_EXPRESSION to "",
        CommandKey.REPLACE_VALUE_BY_VALUE to "Replaces a value by another value.",
        CommandKey.END_REPLACE_VALUE_BY_VALUE to "",
        CommandKey.IF_CONDITION to "Render the enclosed content only if the condition is true.",
        CommandKey.ELSE_IF_CONDITION to "Render the enclosed content only if the condition inside a previously defined if block is true.",
        CommandKey.ELSE_CLAUSE to "Render the enclosed content only if not any of the if/else-if clauses evaluates to true.",
        CommandKey.END_IF_CONDITION to "",
        CommandKey.FOREACH to "Iterates/Loops over a collection of items (=iterable). In each loop, the current item is hold in a loop variable.",
        CommandKey.END_FOREACH to "",
        CommandKey.IGNORE_TEXT to "Ignores the text from the content and does not output it in the template renderer.",
        CommandKey.END_IGNORE_TEXT to "",
        CommandKey.PRINT_TEXT to "Print text as output of the template renderer.",
        CommandKey.STRIP_LINE_AFTER_COMMENT to "slac (=**s**trip **l**ine **a**fter **c**omment) removes all characters and the line break (newline) after the comment. This is useful if you don't want to have empty lines in your template result due to the typical templates comments.",
        CommandKey.STRIP_LINE_BEFORE_COMMENT to "slbc (=**s**trip **l**ine **b**efore **c**omment) removes all characters and the line break (newline) before the comment. This is useful if you don't want to have empty lines in your template result due to the typical templates comments.",
        CommandKey.MODIFY_PROVIDED_FILENAME_BY_REPLACEMENTS to "Each template provide the path of the source file. By using this command, the name will be modified with all replacements provided by ```${CommandKey.REPLACE_VALUE_BY_EXPRESSION.keyword}``` and ```${CommandKey.REPLACE_VALUE_BY_VALUE.keyword}```.",
        CommandKey.RENDER_TEMPLATE to "Calls another template renderer and embeds its output. The first attribute group specifies the renderer class; subsequent groups map model parameters to expressions.",
    )

    // linked map to preserve the order of the keys
    private val commandAttributeKeyDocumentation: Map<CommandAttributeKey, String> = linkedMapOf(
        CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME to "The name of the template class that will generate this template.",
        CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME to "The name of the package where the class defined with ```${CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME.keyAsString}``` resides in.",
        CommandAttributeKey.TEMPLATE_RENDERER_INTERFACE_NAME to "The name of an optional interface class name that is added to the class defined with the ```${CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME.keyAsString}```.",
        CommandAttributeKey.TEMPLATE_RENDERER_INTERFACE_PACKAGE_NAME to "The name of the package where the interface defined with ```${CommandAttributeKey.TEMPLATE_RENDERER_INTERFACE_NAME.keyAsString}``` resides in.",
        CommandAttributeKey.TEMPLATE_MODEL_NAME to "The name of the model variable. The variable can later be used to access fields and functions on the model e.g. in conditions or as replacement values.",
        CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME to "The name of the model class. This class provides all the fields in the template.",
        CommandAttributeKey.TEMPLATE_MODEL_PACKAGE_NAME to "The name of the package where the model class defined with ```${CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME.keyAsString}``` resides in.",
        CommandAttributeKey.SEARCH_VALUE to "The token that has to be searched in the enclosed block of content. The search is case-sensitive.",
        CommandAttributeKey.REPLACE_BY_EXPRESSION to "The expression accessing the model class with which the token defined with the attribute ```${CommandAttributeKey.SEARCH_VALUE.keyAsString}``` is replaced.",
        CommandAttributeKey.REPLACE_BY_VALUE to "The plain value the attribute ```${CommandAttributeKey.SEARCH_VALUE.keyAsString}``` is replaced.",
        CommandAttributeKey.CONDITION_EXPRESSION to "The condition returning a boolean value that is used for the if statement or else-if statement.",
        CommandAttributeKey.LOOP_ITERABLE_EXPRESSION to "The condition returning a boolean value that is used for the if statement.",
        CommandAttributeKey.LOOP_VARIABLE_NAME to "The name of the loop variable, similar to the model variable from ```${CommandAttributeKey.TEMPLATE_MODEL_NAME.keyAsString}```. The variable holds the current instance of the loop iterable defined with ```${CommandAttributeKey.LOOP_ITERABLE_EXPRESSION.keyAsString}```.",
        CommandAttributeKey.TEXT to "Text that is to print as-is into the template renderer.",
        CommandAttributeKey.MODEL_EXPRESSION to "The expression that provides the value for the model parameter specified by ```${CommandAttributeKey.TEMPLATE_MODEL_NAME.keyAsString}``` when calling the template renderer.",
    )

    private fun CommandKey.createMarkDownChapterLink(): String {
        return "[$keyword](#${keyword.lowercase().replace(' ', '-')})"
    }

    fun printMarkdownDocumentation() {
        println("""
            # Keyword/Command reference

            The following keywords/commands are supported:
        """.trimIndent())
        for ((commandKey, _) in commandKeyDocumentation) {
            println("* ${commandKey.createMarkDownChapterLink()}")
        }
        println("")
        println("")


        for ((commandKey, commandKeyDocumentation) in commandKeyDocumentation) {
            println("""

                    ## ${commandKey.keyword}
                    
                    Syntax: ```${commandKey.commandSyntax()}```
                """.trimIndent()
            )
            if(commandKeyDocumentation.isNotBlank()) {
                println("""

                        $commandKeyDocumentation
                    """.trimIndent()
                )
            }
            println("""
                
                    Varia: 
                    * ${commandKey.openingClosingDescription()}
                    * ${commandKey.autoclosingDescription()}
                    * ${commandKey.groupSupportDescription()}
                    * ${commandKey.nestingDescription()}
                """.trimIndent()
            )
            if(commandKey.attributeGroupConstraint == AttributeGroupConstraint.HEADER_WITH_MANY_ATTRIBUTE_GROUPS) {
                println("""
                
                        Header-Attributes:
                    """.trimIndent()
                )
                val attributesDocumentation = commandAttributeKeyDocumentation.filter { it.key in commandKey.allowedHeaderAttributes }
                for((attributeKey, attributeDocumentation) in attributesDocumentation) {
                    println("""
                    * *${attributeKey.keyAsString}*: $attributeDocumentation
                      * Required header attribute: ${(attributeKey in commandKey.headerRequiredAttributes).yesOrNo()}
                      * Required not empty: ${attributeKey.requireNotEmpty.yesOrNo()}
                      * Allowed values: ${if(attributeKey.allowedValues == null) "<unrestricted>" else attributeKey.allowedValues.joinToString(",")}
                    """.trimIndent()
                    )
                }
            }
            if(commandKey.attributeGroupConstraint != AttributeGroupConstraint.NO_ATTRIBUTES) {
                println("""
                
                        Attributes:
                    """.trimIndent()
                )
                val attributesDocumentation = commandAttributeKeyDocumentation.filter { it.key in commandKey.allowedNonHeaderAttributes }
                for((attributeKey, attributeDocumentation) in attributesDocumentation) {
                    println("""
                    * *${attributeKey.keyAsString}*: $attributeDocumentation
                      * Required attribute: ${(attributeKey in commandKey.requiredAttributes).yesOrNo()}
                      * Required not empty: ${attributeKey.requireNotEmpty.yesOrNo()}
                      * Allowed values: ${if(attributeKey.allowedValues == null) "<unrestricted>" else attributeKey.allowedValues.joinToString(",")}
                    """.trimIndent()
                    )
                }
            }
        }
    }
    private fun CommandKey.commandSyntax(): String {
        val sb = StringBuilder()
        sb.append("${COMMAND_PREFIX}${keyword}")
        if(!isClosingCommand) {
            if(attributeGroupConstraint != AttributeGroupConstraint.NO_ATTRIBUTES) {
                if(hasHeaderAttributes) {
                    sb.append(" [ ")
                    (headerRequiredAttributes + headerOptionalAttributes).forEach { attribute ->
                        val attributeValue = if (attribute.allowedValues == null) "..." else attribute.allowedValues.joinToString("|")
                        sb.append("${attribute.keyAsString}=\"${attributeValue}\" ")
                    }
                    sb.append("]")
                    sb.append("[ ")
                    (requiredAttributes + optionalAttributes).forEach { attribute ->
                        val attributeValue = if (attribute.allowedValues == null) "..." else attribute.allowedValues.joinToString("|")
                        sb.append("${attribute.keyAsString}=\"${attributeValue}\" ")
                    }
                    sb.append("]")
                } else {
                    sb.append(" [ ")
                    allowedAttributes.forEach { attribute ->
                        val attributeValue = if (attribute.allowedValues == null) "..." else attribute.allowedValues.joinToString("|")
                        sb.append("${attribute.keyAsString}=\"${attributeValue}\" ")
                    }
                    sb.append("]")
                }
            }
            if(attributeGroupConstraint == AttributeGroupConstraint.MANY_ATTRIBUTE_GROUP
                || attributeGroupConstraint == AttributeGroupConstraint.HEADER_WITH_MANY_ATTRIBUTE_GROUPS) {
                sb.append("[ ... ]")
            }

            correspondingClosingCommandKey?.let { closingKey ->
                sb.append(" .... ${COMMAND_PREFIX}${closingKey.keyword}")
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
        when(attributeGroupConstraint) {
            AttributeGroupConstraint.NO_ATTRIBUTES
                -> "This command/keyword does not support groups and has no attributes."
            AttributeGroupConstraint.ONE_ATTRIBUTE_GROUP
                -> "This command/keyword must have exactly one group of attributes."
            AttributeGroupConstraint.MANY_ATTRIBUTE_GROUP
                -> "This command can have many groups of attributes"
            AttributeGroupConstraint.HEADER_WITH_MANY_ATTRIBUTE_GROUPS
                -> "This command has a header group of attributes followed by one or more groups of attributes."
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
}
