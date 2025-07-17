package org.codeblessing.typicaltemplate.documentation

import org.codeblessing.typicaltemplate.AttributeGroupConstraint
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey

object MarkdownCreator {

    private const val COMMAND_PREFIX = "@@tt-"

    private val commandKeyDocumentation: Map<CommandKey, String> = mapOf(
        CommandKey.TEMPLATE_RENDERER to "Defines in which template the content of the given file is put into. This command must be the first command and can only occur one time per file.",
        CommandKey.REPLACE_VALUE_BY_FIELD to "Defines in which template the content of the given file is put into. This command must be the first command and can only occur one time per file.",
        CommandKey.END_REPLACE_VALUE_BY_FIELD to "",
        CommandKey.IF_FIELD to "Render the enclosed content only if the condition is true",
        CommandKey.END_IF_FIELD to "",
    )

    private val commandAttributeKeyDocumentation: Map<CommandAttributeKey, String> = mapOf(
        CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME to "The name of the template class that will generate this template.",
        CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME to "The name of the package where the class defined with '${CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME.keyAsString}' resides in.",
        CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME to "The name of the model class. This class provides all the fields in the template.",
        CommandAttributeKey.TEMPLATE_MODEL_PACKAGE_NAME to "The name of the package where the model class defined with '${CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME.keyAsString}' resides in.",
        CommandAttributeKey.SEARCH_VALUE to "The token that has to be searched in the enclosed block of content. The search is case-sensitive.",
        CommandAttributeKey.REPLACE_BY_FIELD_NAME to "The field name on the model class with which the token defined with the attribute ${CommandAttributeKey.SEARCH_VALUE.keyAsString} is replaced.",
        CommandAttributeKey.CONDITION_FIELD_NAME to "The field returning a boolean value that is used for the condition.",
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

    private fun Boolean.yesOrNo(): String {
        return if (this) "Yes" else "No"
    }
}
