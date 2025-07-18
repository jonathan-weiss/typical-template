package org.codeblessing.typicaltemplate

import org.codeblessing.typicaltemplate.CommandAttributeKey.*
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

    fun addTemplateRendererCommand(
        templateRendererClassName: String = "MyTemplateClass",
        templateRendererPackageName: String = "org.example.template",
    ): CommandChainBuilder {
        return this.createCommand(CommandKey.TEMPLATE_RENDERER)
            .withAttribute(TEMPLATE_RENDERER_CLASS_NAME, templateRendererClassName)
            .withAttribute(TEMPLATE_RENDERER_PACKAGE_NAME, templateRendererPackageName)
            .addCommandToChain()
    }

    fun addTemplateModel(
        modelName: String = "model",
        modelClassName: String = "MyModelClass",
        modelPackageName: String = "org.example.model",
    ): CommandChainBuilder {
        return this.createCommand(CommandKey.TEMPLATE_MODEL)
            .withAttribute(TEMPLATE_MODEL_NAME, modelName)
            .withAttribute(TEMPLATE_MODEL_CLASS_NAME, modelClassName)
            .withAttribute(TEMPLATE_MODEL_PACKAGE_NAME, modelPackageName)
            .addCommandToChain()
    }

    fun addReplaceValueByExpressionCommand(
        searchValue: String = "search",
        fieldName: String = "myField",
    ): CommandChainBuilder {
        return this.createCommand(CommandKey.REPLACE_VALUE_BY_EXPRESSION)
            .withAttribute(SEARCH_VALUE, searchValue)
            .withAttribute(REPLACE_BY_EXPRESSION, fieldName)
            .addCommandToChain()
    }

    fun addReplaceValueByExpressionCommand(
        vararg replacements: Pair<String, String>,
    ): CommandChainBuilder {
        var builder = this.createCommand(CommandKey.REPLACE_VALUE_BY_EXPRESSION)
        replacements.forEach { (searchValue, fieldName) ->
            builder = builder
                .withAttribute(SEARCH_VALUE, searchValue)
                .withAttribute(REPLACE_BY_EXPRESSION, fieldName)
                .nextAttributeGroup()
        }
        return builder.addCommandToChain()
    }

    fun addEndReplaceValueByExpressionCommand(): CommandChainBuilder {
        return this.createCommand(CommandKey.END_REPLACE_VALUE_BY_EXPRESSION)
            .addCommandToChain()
    }

    fun addIfCommand(
        conditionExpression: String = "myConditionExpression",
    ): CommandChainBuilder {
        return this.createCommand(CommandKey.IF_CONDITION)
            .withAttribute(CONDITION_EXPRESSION, conditionExpression)
            .addCommandToChain()
    }

    fun addElseIfCommand(
        conditionExpression: String = "myOtherConditionExpression",
    ): CommandChainBuilder {
        return this.createCommand(CommandKey.ELSE_IF_CONDITION)
            .withAttribute(CONDITION_EXPRESSION, conditionExpression)
            .addCommandToChain()
    }

    fun addElseCommand(): CommandChainBuilder {
        return this.createCommand(CommandKey.ELSE_CLAUSE)
            .addCommandToChain()
    }

    fun addEndIfCommand(): CommandChainBuilder {
        return this.createCommand(CommandKey.END_IF_CONDITION)
            .addCommandToChain()
    }

    fun addForeachCommand(
        loopVariable: String = "item",
        loopIterable: String = "myList",
    ): CommandChainBuilder {
        return this.createCommand(CommandKey.FOREACH)
            .withAttribute(LOOP_VARIABLE_NAME, loopVariable)
            .withAttribute(LOOP_ITERABLE_EXPRESSION, loopIterable)
            .addCommandToChain()
    }

    fun addEndForeachCommand(): CommandChainBuilder {
        return this.createCommand(CommandKey.END_FOREACH)
            .addCommandToChain()
    }

    fun addIgnoreTextCommand(): CommandChainBuilder {
        return this.createCommand(CommandKey.IGNORE_TEXT)
            .addCommandToChain()
    }

    fun addEndIgnoreTextCommand(): CommandChainBuilder {
        return this.createCommand(CommandKey.END_IGNORE_TEXT)
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


