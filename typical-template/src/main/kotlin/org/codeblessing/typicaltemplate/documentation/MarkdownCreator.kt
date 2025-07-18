package org.codeblessing.typicaltemplate.documentation

import org.codeblessing.typicaltemplate.AttributeGroupConstraint
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey

object MarkdownCreator {

    private const val COMMAND_PREFIX = "@@tt-"

    private val commandKeyDocumentation: Map<CommandKey, String> = mapOf(
        CommandKey.TEMPLATE_RENDERER to "Defines in which template the content of the given file is put into. This command must be the first command and can only occur one time per file.",
        CommandKey.REPLACE_VALUE_BY_EXPRESSION to "Defines in which template the content of the given file is put into. This command must be the first command and can only occur one time per file.",
        CommandKey.END_REPLACE_VALUE_BY_EXPRESSION to "",
        CommandKey.IF_CONDITION to "Render the enclosed content only if the condition is true.",
        CommandKey.ELSE_IF_CONDITION to "Render the enclosed content only if the condition inside a previously defined if block is true.",
        CommandKey.ELSE_CLAUSE to "Render the enclosed content only if not any of the if/else-if clauses evaluates to true.",
        CommandKey.END_IF_CONDITION to "",
        CommandKey.FOREACH to "Iterates/Loops over a collection of items (=iterable). In each loop, the current item is hold in a loop variable.",
        CommandKey.END_FOREACH to "",
        CommandKey.IGNORE_TEXT to "Ignores the text from the content and does not output it in the template renderer.",
        CommandKey.END_IGNORE_TEXT to "",
    )

    private val commandAttributeKeyDocumentation: Map<CommandAttributeKey, String> = mapOf(
        CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME to "The name of the template class that will generate this template.",
        CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME to "The name of the package where the class defined with '${CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME.keyAsString}' resides in.",
        CommandAttributeKey.TEMPLATE_MODEL_NAME to "The name of the model variable. The variable can later be used to access fields and functions on the model e.g. in conditions or as replacement values.",
        CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME to "The name of the model class. This class provides all the fields in the template.",
        CommandAttributeKey.TEMPLATE_MODEL_PACKAGE_NAME to "The name of the package where the model class defined with '${CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME.keyAsString}' resides in.",
        CommandAttributeKey.SEARCH_VALUE to "The token that has to be searched in the enclosed block of content. The search is case-sensitive.",
        CommandAttributeKey.REPLACE_BY_EXPRESSION to "The expression accessing the model class with which the token defined with the attribute '${CommandAttributeKey.SEARCH_VALUE.keyAsString}' is replaced.",
        CommandAttributeKey.CONDITION_EXPRESSION to "The condition returning a boolean value that is used for the if statement or else-if statement.",
        CommandAttributeKey.LOOP_ITERABLE_EXPRESSION to "The condition returning a boolean value that is used for the if statement.",
        CommandAttributeKey.LOOP_VARIABLE_NAME to "The name of the loop variable, similar to the model variable from '${CommandAttributeKey.TEMPLATE_MODEL_NAME.keyAsString}'. The variable holds the current instance of the loop iterable defined with '${CommandAttributeKey.LOOP_ITERABLE_EXPRESSION.keyAsString}'.",
    )

    fun printMarkdownDocumentation() {
        println("---- START MARKDOWN DOCUMENTATION ---")

        println("""
            ### Keywords/Commands

            The following keywords/commands are supported:
        """.trimIndent())

        for ((commandKey, commandKeyDocumentation) in commandKeyDocumentation) {
            println("""

                    #### ${COMMAND_PREFIX}${commandKey.keyword}
                    
                    Syntax: ${commandKey.commandSyntax()}
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
                    * ${commandKey.groupSupportDescription()}
                    * ${commandKey.nestingDescription()}
                """.trimIndent()
            )
            if(commandKey.attributeGroupConstraint != AttributeGroupConstraint.NO_ATTRIBUTES) {
                println("""
                
                        Attributes:
                    """.trimIndent()
                )
                val attributesDocumentation = commandAttributeKeyDocumentation.filter { it.key in commandKey.allowedAttributes }
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

                sb.append(" [ ")
                allowedAttributes.forEach { attribute ->
                    val attributeValue = if (attribute.allowedValues == null) "..." else attribute.allowedValues.joinToString("|")
                    sb.append("${attribute.keyAsString}=\"${attributeValue}\" ")
                }
                sb.append("]")
            }
            if(attributeGroupConstraint == AttributeGroupConstraint.MANY_ATTRIBUTE_GROUP) {
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
            "This command must be closed using the ${COMMAND_PREFIX}${correspondingClosingCommandKey?.keyword} command."
        } else {
            "This command is closing the ${COMMAND_PREFIX}${correspondingOpeningCommandKey?.keyword} command."
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
        }

    private fun CommandKey.nestingDescription(): String =
        if(directlyNestedInsideCommandKey == null) {
            "This command/keyword is NOT forced to reside as nested element in a certain parent element."
        } else {
            "This command/keyword must reside as directly nested element in the parent element ${COMMAND_PREFIX}${directlyNestedInsideCommandKey.keyword}."
        }

    private fun Boolean.yesOrNo(): String {
        return if (this) "Yes" else "No"
    }
}
