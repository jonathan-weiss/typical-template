package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.AttributeGroup
import org.codeblessing.typicaltemplate.AttributeValue
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.fragmenter.CommandFragment
import org.codeblessing.typicaltemplate.contentparsing.fragmenter.TemplateFragment
import org.codeblessing.typicaltemplate.contentparsing.fragmenter.TextFragment
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

class FragmentsBuilder private constructor() {
    companion object {
        fun create(): FragmentsBuilder = FragmentsBuilder()
    }

    private val commandFragments: MutableList<TemplateFragment> = mutableListOf()

    fun addText(text: String): FragmentsBuilder {
        commandFragments.add(TextFragment(createLineNumbers(), text))
        return this
    }

    fun createCommand(commandKey: CommandKey): KeywordCommandBuilder {
        return KeywordCommandBuilder(this, commandKey)
    }

    fun addKeywordCommand(keywordCommand: KeywordCommand): FragmentsBuilder {
        commandFragments.add(CommandFragment(createLineNumbers(), keywordCommand))
        return this
    }

    fun build(): List<TemplateFragment> {
        return commandFragments
    }

    fun addStripLineBeforeCommentCommand(): FragmentsBuilder {
        return this.createCommand(CommandKey.STRIP_LINE_BEFORE_COMMENT)
            .addCommandToChain()
    }

    fun addStripLineAfterCommentCommand(): FragmentsBuilder {
        return this.createCommand(CommandKey.STRIP_LINE_AFTER_COMMENT)
            .addCommandToChain()
    }

    fun addTemplateRendererCommand(
        templateRendererClassName: String = "MyTemplateRendererClass",
        templateRendererPackageName: String = "org.example.template",
    ): FragmentsBuilder {
        return this.createCommand(CommandKey.TEMPLATE_RENDERER)
            .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME, templateRendererClassName)
            .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME, templateRendererPackageName)
            .addCommandToChain()
    }

    fun addTemplateModel(
        modelName: String = "model",
        modelClassName: String = "MyModelClass",
        modelPackageName: String = "org.example.model",
    ): FragmentsBuilder {
        return this.createCommand(CommandKey.TEMPLATE_MODEL)
            .withAttribute(CommandAttributeKey.TEMPLATE_MODEL_NAME, modelName)
            .withAttribute(CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME, modelClassName)
            .withAttribute(CommandAttributeKey.TEMPLATE_MODEL_PACKAGE_NAME, modelPackageName)
            .addCommandToChain()
    }

    fun addReplaceValueByExpressionCommand(
        searchValue: String = "search",
        fieldName: String = "myField",
    ): FragmentsBuilder {
        return this.createCommand(CommandKey.REPLACE_VALUE_BY_EXPRESSION)
            .withAttribute(CommandAttributeKey.SEARCH_VALUE, searchValue)
            .withAttribute(CommandAttributeKey.REPLACE_BY_EXPRESSION, fieldName)
            .addCommandToChain()
    }

    fun addReplaceValueByExpressionCommand(
        vararg replacements: Pair<String, String>,
    ): FragmentsBuilder {
        var builder = this.createCommand(CommandKey.REPLACE_VALUE_BY_EXPRESSION)
        replacements.forEach { (searchValue, fieldName) ->
            builder = builder
                .withAttribute(CommandAttributeKey.SEARCH_VALUE, searchValue)
                .withAttribute(CommandAttributeKey.REPLACE_BY_EXPRESSION, fieldName)
                .nextAttributeGroup()
        }
        return builder.addCommandToChain()
    }

    fun addEndReplaceValueByExpressionCommand(): FragmentsBuilder {
        return this.createCommand(CommandKey.END_REPLACE_VALUE_BY_EXPRESSION)
            .addCommandToChain()
    }

    fun addIfCommand(
        conditionExpression: String = "myConditionExpression",
    ): FragmentsBuilder {
        return this.createCommand(CommandKey.IF_CONDITION)
            .withAttribute(CommandAttributeKey.CONDITION_EXPRESSION, conditionExpression)
            .addCommandToChain()
    }

    fun addElseIfCommand(
        conditionExpression: String = "myOtherConditionExpression",
    ): FragmentsBuilder {
        return this.createCommand(CommandKey.ELSE_IF_CONDITION)
            .withAttribute(CommandAttributeKey.CONDITION_EXPRESSION, conditionExpression)
            .addCommandToChain()
    }

    fun addElseCommand(): FragmentsBuilder {
        return this.createCommand(CommandKey.ELSE_CLAUSE)
            .addCommandToChain()
    }

    fun addEndIfCommand(): FragmentsBuilder {
        return this.createCommand(CommandKey.END_IF_CONDITION)
            .addCommandToChain()
    }

    fun addForeachCommand(
        loopVariable: String = "item",
        loopIterable: String = "myList",
    ): FragmentsBuilder {
        return this.createCommand(CommandKey.FOREACH)
            .withAttribute(CommandAttributeKey.LOOP_VARIABLE_NAME, loopVariable)
            .withAttribute(CommandAttributeKey.LOOP_ITERABLE_EXPRESSION, loopIterable)
            .addCommandToChain()
    }

    fun addEndForeachCommand(): FragmentsBuilder {
        return this.createCommand(CommandKey.END_FOREACH)
            .addCommandToChain()
    }

    fun addIgnoreTextCommand(): FragmentsBuilder {
        return this.createCommand(CommandKey.IGNORE_TEXT)
            .addCommandToChain()
    }

    fun addEndIgnoreTextCommand(): FragmentsBuilder {
        return this.createCommand(CommandKey.END_IGNORE_TEXT)
            .addCommandToChain()
    }

    fun addPrintTextCommand(
        text: String,
    ): FragmentsBuilder {
        return this.createCommand(CommandKey.PRINT_TEXT)
            .withAttribute(CommandAttributeKey.TEXT, text)
            .addCommandToChain()
    }


    class KeywordCommandBuilder(
        private val fragmentsBuilder: FragmentsBuilder,
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

        fun addCommandToChain(): FragmentsBuilder {
            if(attributes.isNotEmpty()) {
                nextAttributeGroup()
            }

            val keywordCommand = KeywordCommand(commandKey, attributeGroups)
            return fragmentsBuilder.addKeywordCommand(keywordCommand)
        }
    }

    private fun createLineNumbers(): LineNumbers {
        return LineNumbers.Companion.EMPTY_LINE_NUMBERS
    }

}
