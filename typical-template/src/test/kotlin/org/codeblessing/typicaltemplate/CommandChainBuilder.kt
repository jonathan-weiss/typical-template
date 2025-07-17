package org.codeblessing.typicaltemplate

import org.codeblessing.typicaltemplate.CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME
import org.codeblessing.typicaltemplate.CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME
import org.codeblessing.typicaltemplate.CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME
import org.codeblessing.typicaltemplate.CommandAttributeKey.TEMPLATE_MODEL_PACKAGE_NAME
import org.codeblessing.typicaltemplate.contentparsing.AttributeGroup
import org.codeblessing.typicaltemplate.contentparsing.CommandFragment
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.LineNumbers
import org.codeblessing.typicaltemplate.contentparsing.LineNumbers.Companion.EMPTY_LINE_NUMBERS
import org.codeblessing.typicaltemplate.contentparsing.TemplateFragment
import org.codeblessing.typicaltemplate.contentparsing.TextFragment

class CommandChainBuilder private constructor() {
    companion object {
        fun create(): CommandChainBuilder = CommandChainBuilder()
    }

    private val commandFragments: MutableList<TemplateFragment> = mutableListOf()

    fun addText(text: String): CommandChainBuilder {
        commandFragments.add(TextFragment(createLineNumbers(), text))
        return this
    }

    fun createCommand(commandKey: CommandKey): KeywordCommandBuilder {
        return KeywordCommandBuilder(this, commandKey)
    }

    fun addKeywordCommand(keywordCommand: KeywordCommand): CommandChainBuilder {
        commandFragments.add(CommandFragment(createLineNumbers(), keywordCommand))
        return this
    }

    fun build(): List<TemplateFragment> {
        return commandFragments
    }

    fun addTemplateCommand(
        templateClassName: String = "MyTemplateClass",
        modelClassName: String = "MyModelClass",
        templatePackageName: String = "org.example.template",
        modelPackageName: String = "org.example.model",
    ): CommandChainBuilder {
        return this.createCommand(CommandKey.TEMPLATE_RENDERER)
            .withAttribute(TEMPLATE_RENDERER_CLASS_NAME, templateClassName)
            .withAttribute(TEMPLATE_RENDERER_PACKAGE_NAME, templatePackageName)
            .withAttribute(TEMPLATE_MODEL_CLASS_NAME, modelClassName)
            .withAttribute(TEMPLATE_MODEL_PACKAGE_NAME, modelPackageName)
            .addCommandToChain()
    }

    fun addReplaceValueByFieldCommand(
        searchValue: String = "search",
        fieldName: String = "myField",
    ): CommandChainBuilder {
        return this.createCommand(CommandKey.REPLACE_VALUE_BY_EXPRESSION)
            .withAttribute(CommandAttributeKey.SEARCH_VALUE, searchValue)
            .withAttribute(CommandAttributeKey.REPLACE_BY_EXPRESSION, fieldName)
            .addCommandToChain()
    }

    fun addReplaceValueByFieldCommand(
        vararg replacements: Pair<String, String>,
    ): CommandChainBuilder {
        var builder = this.createCommand(CommandKey.REPLACE_VALUE_BY_EXPRESSION)
        replacements.forEach { (searchValue, fieldName) ->
            builder = builder
                .withAttribute(CommandAttributeKey.SEARCH_VALUE, searchValue)
                .withAttribute(CommandAttributeKey.REPLACE_BY_EXPRESSION, fieldName)
                .nextAttributeGroup()
        }
        return builder.addCommandToChain()
    }

    fun addEndReplaceValueByFieldCommand(): CommandChainBuilder {
        return this.createCommand(CommandKey.END_REPLACE_VALUE_BY_EXPRESSION)
            .addCommandToChain()
    }

    fun addIfFieldCommand(
        conditionFieldName: String = "myConditionField",
    ): CommandChainBuilder {
        return this.createCommand(CommandKey.IF_CONDITION)
            .withAttribute(CommandAttributeKey.CONDITION_EXPRESSION, conditionFieldName)
            .addCommandToChain()
    }

    fun addEndIfFieldCommand(): CommandChainBuilder {
        return this.createCommand(CommandKey.END_IF_CONDITION)
            .addCommandToChain()
    }


    class KeywordCommandBuilder(
        private val commandChainBuilder: CommandChainBuilder,
        private val commandKey: CommandKey
    ) {

        val attributeGroups: MutableList<AttributeGroup> = mutableListOf()
        var attributes: MutableMap<CommandAttributeKey, AttributeValue> = mutableMapOf()

        fun nextAttributeGroup(): KeywordCommandBuilder {
            attributeGroups.add(AttributeGroup(attributes))
            attributes = mutableMapOf()
            return this
        }

        fun withAttribute(attribute: CommandAttributeKey, value: AttributeValue): KeywordCommandBuilder {
            attributes[attribute] = value
            return this
        }

        fun addCommandToChain(): CommandChainBuilder {
            if(attributes.isNotEmpty()) {
                nextAttributeGroup()
            }

            val keywordCommand = KeywordCommand(commandKey, attributeGroups)
            return commandChainBuilder.addKeywordCommand(keywordCommand)
        }
    }

    private fun createLineNumbers(): LineNumbers {
        return EMPTY_LINE_NUMBERS
    }

}


