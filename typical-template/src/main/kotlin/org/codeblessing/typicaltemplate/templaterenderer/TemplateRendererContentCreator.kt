package org.codeblessing.typicaltemplate.templaterenderer

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.CommandFragment
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.TemplateRenderer
import org.codeblessing.typicaltemplate.contentparsing.TextFragment

object TemplateRendererContentCreator {

    private const val NO_CONTENT_TO_WRITE = ""
    private const val LINE_BREAK = "\n"
    private const val MULTILINE_STRING_DELIMITER = "\"\"\""

    fun createMultilineStringTemplateContent(templateRenderer: TemplateRenderer): String {
        val ctx = TemplateCreationContext(templateRenderer)
        val sb = StringBuilder("|")
        templateRenderer.templateFragments.forEach { templateFragment ->
            when (templateFragment) {
                is TextFragment -> sb.append(rawContent(
                    ctx = ctx,
                    textFragment = templateFragment,
                ))
                is CommandFragment -> sb.append(commandContent(
                    ctx = ctx,
                    command = templateFragment,
                ))
            }
        }
        return sb.toString()
    }

    private fun rawContent(ctx: TemplateCreationContext, textFragment: TextFragment): String {
        if(ctx.nestingStack.isInIgnoreMode()) {
            return NO_CONTENT_TO_WRITE
        }
        return ctx.nestingStack.replaceInString(textFragment.text).addMargin(ctx)
    }

    private fun commandContent(ctx: TemplateCreationContext, command: CommandFragment): String {
        return when (command.keywordCommand.commandKey) {
            CommandKey.TEMPLATE_RENDERER,
            CommandKey.TEMPLATE_MODEL -> throw IllegalArgumentException("Command '${command.keywordCommand.commandKey}' not allowed here")
            CommandKey.REPLACE_VALUE_BY_EXPRESSION -> processReplaceValueByExpression(ctx, command.keywordCommand)
            CommandKey.END_REPLACE_VALUE_BY_EXPRESSION -> processEndReplaceValueByExpression(ctx)
            CommandKey.IF_CONDITION -> processIfCondition(ctx, command.keywordCommand)
            CommandKey.ELSE_IF_CONDITION -> processElseIfCondition(ctx, command.keywordCommand)
            CommandKey.ELSE_CLAUSE -> processElseCondition(ctx)
            CommandKey.END_IF_CONDITION -> processEndIf(ctx)
            CommandKey.FOREACH -> processForeach(ctx, command.keywordCommand)
            CommandKey.END_FOREACH -> processEndForeach(ctx)
            CommandKey.IGNORE_TEXT -> processIgnoreText(ctx, command.keywordCommand)
            CommandKey.END_IGNORE_TEXT -> processEndIgnoreText(ctx)
        }
    }

    private fun processReplaceValueByExpression(ctx: TemplateCreationContext, command: KeywordCommand): String {
        val replacements: Map<String, String> = command.attributeGroups.fold(emptyMap()) { resultMap, attributeGroup ->
            val searchValue = attributeGroup.attribute(CommandAttributeKey.SEARCH_VALUE)
            val placeholderExpression = createExpressionPlaceholder(
                expression = attributeGroup.attribute(CommandAttributeKey.REPLACE_BY_EXPRESSION),
            )
            resultMap + (searchValue to placeholderExpression)
        }
        ctx.nestingStack.pushNestingContext(CommandNestingContext(command, replacements))
        return NO_CONTENT_TO_WRITE
    }

    private fun processEndReplaceValueByExpression(ctx: TemplateCreationContext): String {
        ctx.nestingStack.popNestingContext()
        return NO_CONTENT_TO_WRITE
    }

    private fun processIfCondition(
        ctx: TemplateCreationContext,
        command: KeywordCommand,
    ): String {
        ctx.nestingStack.pushNestingContext(CommandNestingContext(command))
        return startStatementInMultilineText(
            ctx = ctx,
            statement = "if(${command.attribute(CommandAttributeKey.CONDITION_EXPRESSION)})",
        )
    }

    private fun processElseIfCondition(
        ctx: TemplateCreationContext,
        keywordCommand: KeywordCommand,
    ): String {
        return intermediateStatementInMultilineText(
            ctx = ctx,
            statement = "else if(${keywordCommand.attribute(CommandAttributeKey.CONDITION_EXPRESSION)})",
        )
    }

    private fun processElseCondition(
        ctx: TemplateCreationContext,
    ): String {
        ctx.nestingStack.markLastElementHasElseClause()
        return intermediateStatementInMultilineText(
            ctx = ctx,
            statement = "else"
        )
    }

    private fun processEndIf(
        ctx: TemplateCreationContext,
    ): String {
        val hasElseClause = ctx.nestingStack.hasElseClause()
        ctx.nestingStack.popNestingContext()
        if(hasElseClause) {
            return endStatementInMultilineText(ctx = ctx)
        } else {
            val elseClause = intermediateStatementInMultilineText(
                ctx = ctx,
                statement = "else"
            )
            val endIfClause = endStatementInMultilineText(ctx = ctx)

            return elseClause + endIfClause
        }
    }

    private fun processForeach(
        ctx: TemplateCreationContext,
        command: KeywordCommand,
    ): String {
        ctx.nestingStack.pushNestingContext(CommandNestingContext(command))
        return startStatementInMultilineText(
            ctx = ctx,
            statement = "${command.attribute(CommandAttributeKey.LOOP_ITERABLE_EXPRESSION)}.joinToString(\"\") { ${command.attribute(CommandAttributeKey.LOOP_VARIABLE)} -> "
        )
    }

    private fun processEndForeach(
        ctx: TemplateCreationContext,
    ): String {
        ctx.nestingStack.popNestingContext()
        return endStatementInMultilineText(ctx = ctx)
    }

    private fun processIgnoreText(
        ctx: TemplateCreationContext,
        command: KeywordCommand,
    ): String {
        ctx.nestingStack.pushNestingContext(CommandNestingContext(command, isInIgnoreMode = true))
        return NO_CONTENT_TO_WRITE
    }

    private fun processEndIgnoreText(
        ctx: TemplateCreationContext,
    ): String {
        ctx.nestingStack.popNestingContext()
        return NO_CONTENT_TO_WRITE
    }

    private fun startStatementInMultilineText(ctx: TemplateCreationContext, statement: String): String {
        return $$"${ $$statement $${startExpressionBlockWithText(ctx)}"
    }

    private fun intermediateStatementInMultilineText(ctx: TemplateCreationContext, statement: String): String {
        return $$"$${endExpressionBlockWithText(ctx)} $$statement $${startExpressionBlockWithText(ctx)}"
    }

    private fun endStatementInMultilineText(ctx: TemplateCreationContext): String {
        return $$"$${endExpressionBlockWithText(ctx)} }"
    }

    private fun startExpressionBlockWithText(ctx: TemplateCreationContext): String {
        ctx.identLevel.increaseLevel()
        return $$"{ $$MULTILINE_STRING_DELIMITER"
    }

    private fun endExpressionBlockWithText(ctx: TemplateCreationContext): String {
        ctx.identLevel.decreaseLevel()
        return $$"$$LINE_BREAK$$MULTILINE_STRING_DELIMITER }"
    }

    private fun createExpressionPlaceholder(expression: String): String {
        return $$"${$${expression}}"
    }

    private fun String.addIdent(ctx: TemplateCreationContext): String {
        return this.lines()
            .mapIndexed { index, line -> lineWithIdent(line, ctx)}
            .joinToString("\n")
    }

    private fun String.addMargin(ctx: TemplateCreationContext): String {
        return this.lines()
            .joinToString("\n${identAndMarker(ctx, marginSymbol = "|")}")
    }
    private fun identAndMarker(ctx: TemplateCreationContext, marginSymbol: String = ""): String {
        return "${" ".repeat(4 * ctx.identLevel.identLevel)}$marginSymbol"
    }

    private fun lineWithIdent(line: String, ctx: TemplateCreationContext, marginSymbol: String = ""): String {
        return "${" ".repeat(4 * ctx.identLevel.identLevel)}$marginSymbol$line"
    }

    private data class TemplateCreationContext(
        val templateRenderer: TemplateRenderer,
        val identLevel: IdentLevel = IdentLevel(),
        val nestingStack: CommandNestingContextStack = CommandNestingContextStack()
    )

    private class IdentLevel {
        private var level = 0

        val identLevel: Int
            get() = level

        fun increaseLevel(): IdentLevel {
            level++
            return this
        }

        fun decreaseLevel(): IdentLevel {
            level--
            return this
        }
    }

    private class CommandNestingContextStack {
        private val nestingStack: MutableList<CommandNestingContext> = mutableListOf()

        fun pushNestingContext(ctx: CommandNestingContext) {
            nestingStack.add(ctx)
        }

        fun popNestingContext() {
            nestingStack.removeLast()
        }

        fun replaceInString(text: String): String {
            var resultText = text

            for((searchValue, replacementValue) in createReplacementMap()) {
                resultText = resultText.replace(searchValue, replacementValue)
            }
            return resultText
        }

        private fun createReplacementMap(): Map<String, String> {
            return nestingStack.fold(emptyMap()) { acc, nestingCtx ->
                acc + nestingCtx.replacements
            }
        }

        fun markLastElementHasElseClause() {
            nestingStack.last().markLastElementHasElseClause()
        }


        fun hasElseClause(): Boolean {
            return nestingStack.last().hasElseClause
        }

        fun isInIgnoreMode(): Boolean {
            return nestingStack.any { it.isInIgnoreMode }
        }

    }

    private class CommandNestingContext(
        val command: KeywordCommand,
        val replacements: Map<String, String> = emptyMap(),
        var hasElseClause: Boolean = false,
        var isInIgnoreMode: Boolean = false,
    ) {
        fun markLastElementHasElseClause() {
            require(command.commandKey == CommandKey.IF_CONDITION) {
                "try to change the 'hasElseClause' flag " +
                        "but nesting element is not ${CommandKey.IF_CONDITION} but ${command.commandKey}"
            }
            hasElseClause = true
        }

    }
}
