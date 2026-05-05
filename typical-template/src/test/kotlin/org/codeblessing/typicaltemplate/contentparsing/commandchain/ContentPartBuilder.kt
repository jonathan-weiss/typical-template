package org.codeblessing.typicaltemplate.contentparsing.commandchain

import org.codeblessing.typicaltemplate.AttributeGroup
import org.codeblessing.typicaltemplate.AttributeValue
import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.DirectionValue
import org.codeblessing.typicaltemplate.ExpandModeValue
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
            return addTemplateRendererCommandWithTemplateModel(
                templateRendererClassName = templateRendererClassName,
                templateRendererPackageName = templateRendererPackageName,
            ).end()
        }

        fun addTemplateRendererCommandWithTemplateModel(
            templateRendererClassName: String = "MyTemplateRendererClass",
            templateRendererPackageName: String = "org.example.template",
        ): TemplateRendererBuilder {
            return TemplateRendererBuilder(this, templateRendererClassName, templateRendererPackageName)
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

        fun addMoveCommentCommand(
            direction: DirectionValue = DirectionValue.FORWARD,
            beforeFirstOccurrenceOf: String? = null,
            afterFirstOccurrenceOf: String? = null,
            beforeLastOccurrenceOf: String? = null,
            afterLastOccurrenceOf: String? = null,
        ): TemplateCommentBuilder {
            var builder = createCommand(CommandKey.MOVE_COMMENT)
                .withAttribute(CommandAttributeKey.DIRECTION, direction.value)
            beforeFirstOccurrenceOf?.let { builder = builder.withAttribute(CommandAttributeKey.BEFORE_FIRST_OCCURRENCE_OF, it) }
            afterFirstOccurrenceOf?.let { builder = builder.withAttribute(CommandAttributeKey.AFTER_FIRST_OCCURRENCE_OF, it) }
            beforeLastOccurrenceOf?.let { builder = builder.withAttribute(CommandAttributeKey.BEFORE_LAST_OCCURRENCE_OF, it) }
            afterLastOccurrenceOf?.let { builder = builder.withAttribute(CommandAttributeKey.AFTER_LAST_OCCURRENCE_OF, it) }
            return builder.addCommandToChain()
        }

        fun addExpandCommentCommand(
            direction: DirectionValue = DirectionValue.FORWARD,
            stripMode: ExpandModeValue = ExpandModeValue.BLANKS,
        ): TemplateCommentBuilder {
            return createCommand(CommandKey.EXPAND_COMMENT)
                .withAttribute(CommandAttributeKey.EXPAND_DIRECTION, direction.value)
                .withAttribute(CommandAttributeKey.STRIP_MODE, stripMode.value)
                .addCommandToChain()
        }

        fun addPrintTextCommand(text: String): TemplateCommentBuilder {
            return this.createCommand(CommandKey.PRINT_TEXT)
                .withAttribute(CommandAttributeKey.TEXT, text)
                .addCommandToChain()
        }

        class TemplateRendererBuilder(
            private val templateCommentBuilder: TemplateCommentBuilder,
            private val rendererClassName: String,
            private val rendererPackageName: String,
        ) {
            private val modelAttributeGroups: MutableList<AttributeGroup> = mutableListOf()

            fun addTemplateModel(
                modelName: String = "model",
                modelClassName: String = "MyModelClass",
                modelPackageName: String = "org.example.model",
                isList: Boolean = false,
            ): TemplateRendererBuilder {
                val modelAttributes = mutableMapOf<CommandAttributeKey, AttributeValue>(
                    CommandAttributeKey.TEMPLATE_MODEL_NAME to modelName,
                    CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME to modelClassName,
                    CommandAttributeKey.TEMPLATE_MODEL_PACKAGE_NAME to modelPackageName,
                )
                if (isList) {
                    modelAttributes[CommandAttributeKey.TEMPLATE_MODEL_IS_LIST] = "true"
                }
                modelAttributeGroups.add(AttributeGroup(modelAttributes))
                return this
            }

            fun end(): TemplateCommentBuilder {
                val rendererAttributes = mutableMapOf<CommandAttributeKey, AttributeValue>(
                    CommandAttributeKey.TEMPLATE_RENDERER_CLASS_NAME to rendererClassName,
                    CommandAttributeKey.TEMPLATE_RENDERER_PACKAGE_NAME to rendererPackageName,
                )
                val allAttributeGroups = listOf(AttributeGroup(rendererAttributes)) + modelAttributeGroups
                val keywordCommand = KeywordCommand(CommandKey.TEMPLATE_RENDERER, allAttributeGroups)
                return templateCommentBuilder.addKeywordCommand(keywordCommand)
            }
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
