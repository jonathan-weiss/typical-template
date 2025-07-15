package org.codeblessing.typicaltemplate.template

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.CommandFragment
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.Template
import org.codeblessing.typicaltemplate.contentparsing.TextFragment

object TemplateContentCreator {

    private const val NO_CONTENT_TO_WRITE = ""
    private const val LINE_BREAK = "\n"
    private const val MULTILINE_STRING_DELIMITER = "\"\"\""

    fun createTemplateContent(template: Template): String {
        val ctx = TemplateCreationContext(template)
        val sb = StringBuilder()
        sb.append(MULTILINE_STRING_DELIMITER + LINE_BREAK)
        template.templateFragments.forEach { templateFragment ->
            when (templateFragment) {
                is TextFragment -> sb.append(rawContent(
                    ctx = ctx,
                    textFragment = templateFragment,
                ))
                is CommandFragment -> sb.append(commandContent(
                    ctx = ctx,
                    command = templateFragment,
                    modelName = template.modelName,
                ))
            }
        }
        sb.append(LINE_BREAK + MULTILINE_STRING_DELIMITER)
        return sb.toString()
    }

    private fun rawContent(ctx: TemplateCreationContext, textFragment: TextFragment): String {
        return ctx.tokenReplacementStack.replaceInString(textFragment.text)
    }

    private fun commandContent(ctx: TemplateCreationContext, command: CommandFragment, modelName: String): String {
        return when (command.keywordCommand.commandKey) {
            CommandKey.TEMPLATE -> throw IllegalArgumentException("Template command not allowed here")
            CommandKey.REPLACE_VALUE_BY_FIELD -> processReplaceValueByField(ctx, command.keywordCommand, modelName)
            CommandKey.END_REPLACE_VALUE_BY_FIELD -> processEndReplaceValueByField(ctx)
            CommandKey.IF_FIELD -> processIfField(ctx, command.keywordCommand, modelName)
            CommandKey.END_IF_FIELD -> processEndIfWithoutElseField(ctx)
        }
    }

    private fun processReplaceValueByField(ctx: TemplateCreationContext, command: KeywordCommand, modelName: String): String {
        val replacements: Map<String, String> = command.attributeGroups.fold(emptyMap()) { resultMap, attributeGroup ->
            val searchValue = attributeGroup.attribute(CommandAttributeKey.SEARCH_VALUE)
            val fieldPlaceholderExpression = createFieldPlaceholder(
                modelName = modelName,
                fieldName = attributeGroup.attribute(CommandAttributeKey.REPLACE_BY_FIELD_NAME),
            )
            resultMap + (searchValue to fieldPlaceholderExpression)
        }
        ctx.tokenReplacementStack.pushReplacements(replacements)
        return NO_CONTENT_TO_WRITE
    }

    private fun processEndReplaceValueByField(ctx: TemplateCreationContext): String {
        ctx.tokenReplacementStack.popReplacements()
        return NO_CONTENT_TO_WRITE
    }

    private fun processIfField(
        ctx: TemplateCreationContext,
        keywordCommand: KeywordCommand,
        modelName: String,
    ): String {
        return startStatementInMultilineText(
            ctx = ctx,
            statement = "if(${modelName}.${keywordCommand.attribute(CommandAttributeKey.CONDITION_FIELD_NAME)})",
        )
    }

    private fun processEndIfContainingElseField(
        ctx: TemplateCreationContext,
    ): String {
        return endStatementInMultilineText(ctx = ctx)
    }

    private fun processEndIfWithoutElseField(
        ctx: TemplateCreationContext,
    ): String {
        val elseClause = intermediateStatementInMultilineText(
            ctx = ctx,
            statement = "else"
        )
        val endIfClause = endStatementInMultilineText(ctx = ctx)

        return elseClause + endIfClause
    }

    private fun startStatementInMultilineText(ctx: TemplateCreationContext, statement: String): String {
        return $$"${ $$statement $${startExpressionBlockWithText(ctx)}"
    }

    private fun intermediateStatementInMultilineText(ctx: TemplateCreationContext, statement: String): String {
        return $$"$${endExpressionBlockWithText(ctx)}  $$statement $${startExpressionBlockWithText(ctx)}"
    }

    private fun endStatementInMultilineText(ctx: TemplateCreationContext): String {
        return $$"$${endExpressionBlockWithText(ctx)} }"
    }

    private fun startExpressionBlockWithText(ctx: TemplateCreationContext): String {
        return $$" { $$MULTILINE_STRING_DELIMITER$$LINE_BREAK"
    }

    private fun endExpressionBlockWithText(ctx: TemplateCreationContext): String {
        return $$"$$MULTILINE_STRING_DELIMITER$$LINE_BREAK }"
    }

    private fun createFieldPlaceholder(modelName: String, fieldName: String): String {
        return "${'$'}{${modelName}.${fieldName}}"
    }

    private fun addMargin(text: String): String {
        return text.lines()
            .mapIndexed { index, line -> lineWithMargin(line, index > 0)} // not the first line
            .joinToString("\n")
    }

    private fun lineWithMargin(line: String, hasIdent: Boolean): String {
        val hasIdentFactor = if(hasIdent) 1 else 0
        return "${" ".repeat(4 * 6 * hasIdentFactor)}|$line"
    }

    private data class TemplateCreationContext(
        val template: Template,
        val identLevel: IdentLevel = IdentLevel(),
        val tokenReplacementStack: TokenReplacementsStack = TokenReplacementsStack()
    )

    private class IdentLevel

    private class TokenReplacementsStack {
        private val replacementsStack: MutableList<Map<String, String>> = mutableListOf()

        fun replaceInString(text: String): String {
            var resultText = text

            for((searchValue, replacementValue) in createReplacementMap()) {
                resultText = resultText.replace(searchValue, replacementValue)
            }
            return resultText
        }

        private fun createReplacementMap(): Map<String, String> {
            return replacementsStack.fold(emptyMap()) { acc, replacementMap ->
                acc + replacementMap
            }
        }

        fun pushReplacements(replacements: Map<String, String>) {
            replacementsStack.add(replacements)
        }

        fun popReplacements() {
            replacementsStack.removeLast()
        }
    }
}
