package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.AttributeGroup
import org.codeblessing.typicaltemplate.AttributeValue
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateCommentContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TemplateContentPart
import org.codeblessing.typicaltemplate.contentparsing.resolver.TextContentPart
import org.codeblessing.typicaltemplate.contentparsing.linenumbers.LineNumbers

class ContentPartBuilder private constructor() {
    companion object {
        fun create(): ContentPartBuilder = ContentPartBuilder()
    }

    private val contentParts: MutableList<TemplateContentPart> = mutableListOf()

    fun addText(text: String): ContentPartBuilder {
        contentParts.add(TextContentPart(createLineNumbers(), text))
        return this
    }

    fun addTemplateComment(): TemplateCommentBuilder {
        return TemplateCommentBuilder(this)
    }

    internal fun finishTemplateComment(keywordCommands: List<KeywordCommand>) {
        contentParts.add(TemplateCommentContentPart(createLineNumbers(), keywordCommands))
    }

    fun build(): List<TemplateContentPart> = contentParts

    private fun createLineNumbers(): LineNumbers = LineNumbers.EMPTY_LINE_NUMBERS

    class TemplateCommentBuilder(private val contentPartBuilder: ContentPartBuilder) {
        private val keywordCommands: MutableList<KeywordCommand> = mutableListOf()

        internal fun addKeywordCommand(keywordCommand: KeywordCommand): TemplateCommentBuilder {
            keywordCommands.add(keywordCommand)
            return this
        }

        fun createCommand(commandKey: CommandKey): KeywordCommandBuilder {
            return KeywordCommandBuilder(this, commandKey)
        }

        fun end(): ContentPartBuilder {
            contentPartBuilder.finishTemplateComment(keywordCommands)
            return contentPartBuilder
        }

        fun addTemplateRendererCommand(
            templateRendererClassName: String = "MyTemplateRendererClass",
            templateRendererPackageName: String = "org.example.template",
        ): TemplateCommentBuilder {
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
        ): TemplateCommentBuilder {
            val lastCommand = keywordCommands.removeLast()
            require(lastCommand.commandKey == CommandKey.TEMPLATE_RENDERER)
            val modelAttributes = mutableMapOf<CommandAttributeKey, AttributeValue>(
                CommandAttributeKey.TEMPLATE_MODEL_NAME to modelName,
                CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME to modelClassName,
                CommandAttributeKey.TEMPLATE_MODEL_PACKAGE_NAME to modelPackageName,
            )
            if (isList) {
                modelAttributes[CommandAttributeKey.TEMPLATE_MODEL_IS_LIST] = "true"
            }
            val updatedCommand = KeywordCommand(CommandKey.TEMPLATE_RENDERER, lastCommand.attributeGroups + AttributeGroup(modelAttributes))
            keywordCommands.add(updatedCommand)
            return this
        }

        fun addReplaceValueByExpressionCommand(
            searchValue: String = "search",
            fieldName: String = "myField",
        ): TemplateCommentBuilder {
            return this.createCommand(CommandKey.REPLACE_VALUE_BY_EXPRESSION)
                .withAttribute(CommandAttributeKey.SEARCH_VALUE, searchValue)
                .withAttribute(CommandAttributeKey.REPLACE_BY_EXPRESSION, fieldName)
                .addCommandToChain()
        }

        fun addReplaceValueByExpressionCommand(
            vararg replacements: Pair<String, String>,
        ): TemplateCommentBuilder {
            var builder = this.createCommand(CommandKey.REPLACE_VALUE_BY_EXPRESSION)
            replacements.forEach { (searchValue, fieldName) ->
                builder = builder
                    .withAttribute(CommandAttributeKey.SEARCH_VALUE, searchValue)
                    .withAttribute(CommandAttributeKey.REPLACE_BY_EXPRESSION, fieldName)
                    .nextAttributeGroup()
            }
            return builder.addCommandToChain()
        }

        fun addEndReplaceValueByExpressionCommand(): TemplateCommentBuilder {
            return this.createCommand(CommandKey.END_REPLACE_VALUE_BY_EXPRESSION).addCommandToChain()
        }

        fun addIfCommand(
            conditionExpression: String = "myConditionExpression",
        ): TemplateCommentBuilder {
            return this.createCommand(CommandKey.IF_CONDITION)
                .withAttribute(CommandAttributeKey.CONDITION_EXPRESSION, conditionExpression)
                .addCommandToChain()
        }

        fun addElseIfCommand(
            conditionExpression: String = "myOtherConditionExpression",
        ): TemplateCommentBuilder {
            return this.createCommand(CommandKey.ELSE_IF_CONDITION)
                .withAttribute(CommandAttributeKey.CONDITION_EXPRESSION, conditionExpression)
                .addCommandToChain()
        }

        fun addElseCommand(): TemplateCommentBuilder {
            return this.createCommand(CommandKey.ELSE_CLAUSE).addCommandToChain()
        }

        fun addEndIfCommand(): TemplateCommentBuilder {
            return this.createCommand(CommandKey.END_IF_CONDITION).addCommandToChain()
        }

        fun addForeachCommand(
            loopVariable: String = "item",
            loopIterable: String = "myList",
        ): TemplateCommentBuilder {
            return this.createCommand(CommandKey.FOREACH)
                .withAttribute(CommandAttributeKey.LOOP_VARIABLE_NAME, loopVariable)
                .withAttribute(CommandAttributeKey.LOOP_ITERABLE_EXPRESSION, loopIterable)
                .addCommandToChain()
        }

        fun addEndForeachCommand(): TemplateCommentBuilder {
            return this.createCommand(CommandKey.END_FOREACH).addCommandToChain()
        }

        fun addIgnoreTextCommand(): TemplateCommentBuilder {
            return this.createCommand(CommandKey.IGNORE_TEXT).addCommandToChain()
        }

        fun addEndIgnoreTextCommand(): TemplateCommentBuilder {
            return this.createCommand(CommandKey.END_IGNORE_TEXT).addCommandToChain()
        }

        fun addEndTemplateRendererCommand(): TemplateCommentBuilder {
            return this.createCommand(CommandKey.END_TEMPLATE_RENDERER).addCommandToChain()
        }

        fun addPrintTextCommand(text: String): TemplateCommentBuilder {
            return this.createCommand(CommandKey.PRINT_TEXT)
                .withAttribute(CommandAttributeKey.TEXT, text)
                .addCommandToChain()
        }
    }

    class KeywordCommandBuilder(
        private val templateCommentBuilder: TemplateCommentBuilder,
        private val commandKey: CommandKey,
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

        fun addCommandToChain(): TemplateCommentBuilder {
            if (attributes.isNotEmpty()) {
                nextAttributeGroup()
            }
            val keywordCommand = KeywordCommand(commandKey, attributeGroups)
            return templateCommentBuilder.addKeywordCommand(keywordCommand)
        }
    }
}
