package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.LineNumbers.Companion.EMPTY_LINE_NUMBERS

object CommandChainValidator {

    private const val DEFAULT_PACKAGE_NAME = ""

    fun validateCommands(templateFragments: List<TemplateFragment>): List<Template> {
        val keywordCommand: KeywordCommand = assureFirstAndOnlyCommandIsTemplateDefinition(templateFragments)


        val templateFragmentsToApply = mutableListOf<TemplateFragment>()

        val openingCommandKeysStack: MutableList<CommandKey> = mutableListOf()
        templateFragments.filterNot { it.isTemplateDefinitionCommand() }.forEach { templateFragment ->
            if(templateFragment is CommandFragment) {
                val commandKey = templateFragment.keywordCommand.commandKey
                if(commandKey.isOpeningCommand) {
                    openingCommandKeysStack.add(commandKey)
                } else if(commandKey.isClosingCommand) {
                    val correspondingOpeningCommand = openingCommandKeysStack.removeLastOrNull()
                    if(correspondingOpeningCommand != commandKey.correspondingOpeningCommandKey) {
                        throw TemplateParsingException(
                            lineNumbers = templateFragment.lineNumbers,
                            msg = "The template has a closing command '${commandKey.keyword}' " +
                                    "without a corresponding opening command '${commandKey.correspondingOpeningCommandKey?.keyword}'" +
                                    "before in the template."
                        )
                    }
                }
            }

            templateFragmentsToApply.add(templateFragment)
        }

        if(openingCommandKeysStack.isNotEmpty()) {
            throw TemplateParsingException(
                lineNumbers = EMPTY_LINE_NUMBERS,
                msg = "The template has the following opening commands ${openingCommandKeysStack.map { it.keyword }} " +
                        "that needs to be closed using the following closing commands " +
                        "${openingCommandKeysStack.map { it.correspondingClosingCommandKey?.keyword }}."
            )
        }

        val template = Template(
            templateClassName = keywordCommand.attribute(CommandAttributeKey.TEMPLATE_CLASS_NAME),
            templateClassPackage = keywordCommand.attributeOptional(CommandAttributeKey.TEMPLATE_CLASS_PACKAGE_NAME) ?: DEFAULT_PACKAGE_NAME,
            modelClassName = keywordCommand.attribute(CommandAttributeKey.TEMPLATE_MODEL_CLASS_NAME),
            modelClassPackage = keywordCommand.attributeOptional(CommandAttributeKey.TEMPLATE_MODEL_CLASS_PACKAGE_NAME) ?: DEFAULT_PACKAGE_NAME,
            templateFragments = templateFragmentsToApply
        )
        return listOf(template)

    }

    private fun assureFirstAndOnlyCommandIsTemplateDefinition(
        templateFragments: List<TemplateFragment>
    ): KeywordCommand {
        val count = templateFragments.count { it.isTemplateDefinitionCommand() }

        if(count != 1) {
            throw TemplateParsingException(
                lineNumbers = templateFragments.firstOrNull()?.lineNumbers ?: EMPTY_LINE_NUMBERS,
                msg = "There must be exactly one template command '${CommandKey.TEMPLATE.keyword}'. ",
            )
        }

        val commandFragment = templateFragments
            .filterIsInstance<CommandFragment>()
            .first()

        if(!commandFragment.isTemplateDefinitionCommand()) {
            throw TemplateParsingException(
                lineNumbers = commandFragment.lineNumbers,
                msg = "The first command in a file must be '${CommandKey.TEMPLATE.keyword}' " +
                        "but was ${commandFragment.keywordCommand.commandKey.keyword}. ",
            )
        }
        return commandFragment.keywordCommand
    }

    private fun TemplateFragment.isTemplateDefinitionCommand(): Boolean {
        return this is CommandFragment && this.keywordCommand.commandKey == CommandKey.TEMPLATE
    }
}
