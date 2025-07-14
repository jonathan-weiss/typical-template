package org.codeblessing.typicaltemplate.template

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.CommandFragment
import org.codeblessing.typicaltemplate.contentparsing.KeywordCommand
import org.codeblessing.typicaltemplate.contentparsing.Template
import org.codeblessing.typicaltemplate.contentparsing.TextFragment

object TemplateContentCreator {

    private const val NO_CONTENT_TO_WRITE = ""

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

    private val tokenReplacementStack = TokenReplacementsStack()

    fun createTemplateContent(template: Template): String {
        val sb = StringBuilder()
        template.templateFragments.forEach { templateFragment ->
            when (templateFragment) {
                is TextFragment -> sb.append(rawContent(
                    textFragment = templateFragment,
                ))
                is CommandFragment -> sb.append(commandContent(
                    command = templateFragment,
                    modelName = template.modelName
                ))
            }
        }
        return sb.toString()
    }

    private fun rawContent(textFragment: TextFragment): String {
        return tokenReplacementStack.replaceInString(textFragment.text)
    }

    private fun commandContent(command: CommandFragment, modelName: String): String {
        return when (command.keywordCommand.commandKey) {
            CommandKey.TEMPLATE -> throw IllegalArgumentException("Template command not allowed here")
            CommandKey.REPLACE_VALUE_BY_FIELD -> processReplaceValueByField(command.keywordCommand, modelName)
            CommandKey.END_REPLACE_VALUE_BY_FIELD -> processEndReplaceValueByField()
            CommandKey.IF_FIELD -> processIfField(command.keywordCommand, modelName)
            CommandKey.END_IF_FIELD -> processEndIfField()
        }
    }

    private fun processReplaceValueByField(command: KeywordCommand, modelName: String): String {
        val replacements: Map<String, String> = command.attributeGroups.fold(emptyMap()) { resultMap, attributeGroup ->
            val searchValue = attributeGroup.attribute(CommandAttributeKey.SEARCH_VALUE)
            val fieldPlaceholderExpression = createFieldPlaceholder(
                modelName = modelName,
                fieldName = attributeGroup.attribute(CommandAttributeKey.REPLACE_BY_FIELD_NAME),
            )
            resultMap + (searchValue to fieldPlaceholderExpression)
        }
        tokenReplacementStack.pushReplacements(replacements)
        return NO_CONTENT_TO_WRITE
    }

    private fun processEndReplaceValueByField(): String {
        tokenReplacementStack.popReplacements()
        return NO_CONTENT_TO_WRITE
    }

    private fun processIfField(keywordCommand: KeywordCommand, modelName: String): String {
        return """${startMultilineStringCommand()}if(${modelName}.${keywordCommand.attribute(CommandAttributeKey.CONDITION_FIELD_NAME)}) ""${'"'}"""
    }

    private fun processEndIfField(): String {
        return """}""${'"'} else ""${endMultilineStringCommand()}"""
    }

    private fun startMultilineStringCommand(): String {
        return "\${"
    }

    private fun endMultilineStringCommand(): String {
        return "}"
    }

    private fun createFieldPlaceholder(modelName: String, fieldName: String): String {
        return "${'$'}{${modelName}.${fieldName}}"
    }
}
