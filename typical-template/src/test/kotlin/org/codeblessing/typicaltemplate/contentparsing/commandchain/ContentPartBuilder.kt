package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.AttributeGroup
import org.codeblessing.typicaltemplate.AttributeValue
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.resolver.CommandContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TextContentPart
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

class ContentPartBuilder private constructor() {
    companion object {
        fun create(): ContentPartBuilder = ContentPartBuilder()
    }

    private val commandContentParts: MutableList<TemplateContentPart> = mutableListOf()

    fun addText(text: String): ContentPartBuilder {
        commandContentParts.add(TextContentPart(createLineNumbers(), text))
        return this
    }

    fun createCommand(commandKey: CommandKey): KeywordCommandBuilder {
        return KeywordCommandBuilder(this, commandKey)
    }

    fun addKeywordCommand(keywordCommand: KeywordCommand): ContentPartBuilder {
        commandContentParts.add(CommandContentPart(createLineNumbers(), keywordCommand))
        return this
    }

    fun build(): List<TemplateContentPart> {
        return commandContentParts
    }

    fun addStripLineBeforeCommentCommand(): ContentPartBuilder {
        return this.createCommand(CommandKey.STRIP_LINE_BEFORE_COMMENT)
            .addCommandToChain()
    }

    fun addStripLineAfterCommentCommand(): ContentPartBuilder {
        return this.createCommand(CommandKey.STRIP_LINE_AFTER_COMMENT)
            .addCommandToChain()
    }

    fun addTemplateRendererCommand(
        templateRendererClassName: String = "MyTemplateRendererClass",
        templateRendererPackageName: String = "org.example.template",
    ): ContentPartBuilder {
        return this.createCommand(CommandKey.TEMPLATE_RENDERER)
            .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME, templateRendererClassName)
            .withAttribute(CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME, templateRendererPackageName)
            .addCommandToChain()
    }

    fun addTemplateModel(
        modelName: String = "model",
        modelClassName: String = "MyModelClass",
        modelPackageName: String = "org.example.model",
        isList: Boolean = false,
    ): ContentPartBuilder {
        val builder = this.createCommand(CommandKey.TEMPLATE_MODEL)
            .withAttribute(CommandAttributeKey.TEMPLATE_MODEL_NAME, modelName)
            .withAttribute(CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME, modelClassName)
            .withAttribute(CommandAttributeKey.TEMPLATE_MODEL_PACKAGE_NAME, modelPackageName)
        if (isList) {
            builder.withAttribute(CommandAttributeKey.TEMPLATE_MODEL_IS_LIST, "true")
        }
        return builder.addCommandToChain()
    }

    fun addReplaceValueByExpressionCommand(
        searchValue: String = "search",
        fieldName: String = "myField",
    ): ContentPartBuilder {
        return this.createCommand(CommandKey.REPLACE_VALUE_BY_EXPRESSION)
            .withAttribute(CommandAttributeKey.SEARCH_VALUE, searchValue)
            .withAttribute(CommandAttributeKey.REPLACE_BY_EXPRESSION, fieldName)
            .addCommandToChain()
    }

    fun addReplaceValueByExpressionCommand(
        vararg replacements: Pair<String, String>,
    ): ContentPartBuilder {
        var builder = this.createCommand(CommandKey.REPLACE_VALUE_BY_EXPRESSION)
        replacements.forEach { (searchValue, fieldName) ->
            builder = builder
                .withAttribute(CommandAttributeKey.SEARCH_VALUE, searchValue)
                .withAttribute(CommandAttributeKey.REPLACE_BY_EXPRESSION, fieldName)
                .nextAttributeGroup()
        }
        return builder.addCommandToChain()
    }

    fun addEndReplaceValueByExpressionCommand(): ContentPartBuilder {
        return this.createCommand(CommandKey.END_REPLACE_VALUE_BY_EXPRESSION)
            .addCommandToChain()
    }

    fun addIfCommand(
        conditionExpression: String = "myConditionExpression",
    ): ContentPartBuilder {
        return this.createCommand(CommandKey.IF_CONDITION)
            .withAttribute(CommandAttributeKey.CONDITION_EXPRESSION, conditionExpression)
            .addCommandToChain()
    }

    fun addElseIfCommand(
        conditionExpression: String = "myOtherConditionExpression",
    ): ContentPartBuilder {
        return this.createCommand(CommandKey.ELSE_IF_CONDITION)
            .withAttribute(CommandAttributeKey.CONDITION_EXPRESSION, conditionExpression)
            .addCommandToChain()
    }

    fun addElseCommand(): ContentPartBuilder {
        return this.createCommand(CommandKey.ELSE_CLAUSE)
            .addCommandToChain()
    }

    fun addEndIfCommand(): ContentPartBuilder {
        return this.createCommand(CommandKey.END_IF_CONDITION)
            .addCommandToChain()
    }

    fun addForeachCommand(
        loopVariable: String = "item",
        loopIterable: String = "myList",
    ): ContentPartBuilder {
        return this.createCommand(CommandKey.FOREACH)
            .withAttribute(CommandAttributeKey.LOOP_VARIABLE_NAME, loopVariable)
            .withAttribute(CommandAttributeKey.LOOP_ITERABLE_EXPRESSION, loopIterable)
            .addCommandToChain()
    }

    fun addEndForeachCommand(): ContentPartBuilder {
        return this.createCommand(CommandKey.END_FOREACH)
            .addCommandToChain()
    }

    fun addIgnoreTextCommand(): ContentPartBuilder {
        return this.createCommand(CommandKey.IGNORE_TEXT)
            .addCommandToChain()
    }

    fun addEndIgnoreTextCommand(): ContentPartBuilder {
        return this.createCommand(CommandKey.END_IGNORE_TEXT)
            .addCommandToChain()
    }

    fun addEndTemplateRendererCommand(): ContentPartBuilder {
        return this.createCommand(CommandKey.END_TEMPLATE_RENDERER)
            .addCommandToChain()
    }

    fun addPrintTextCommand(
        text: String,
    ): ContentPartBuilder {
        return this.createCommand(CommandKey.PRINT_TEXT)
            .withAttribute(CommandAttributeKey.TEXT, text)
            .addCommandToChain()
    }


    class KeywordCommandBuilder(
        private val contentPartBuilder: ContentPartBuilder,
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

        fun addCommandToChain(): ContentPartBuilder {
            if(attributes.isNotEmpty()) {
                nextAttributeGroup()
            }

            val keywordCommand = KeywordCommand(commandKey, attributeGroups)
            return contentPartBuilder.addKeywordCommand(keywordCommand)
        }
    }

    private fun createLineNumbers(): LineNumbers {
        return LineNumbers.EMPTY_LINE_NUMBERS
    }

}
