package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.CommandAttributeKey
import org.codeblessing.typicaltemplate.CommandAttributeKey.*
import org.codeblessing.typicaltemplate.CommandKey
import org.codeblessing.typicaltemplate.contentparsing.LineNumbers.Companion.EMPTY_LINE_NUMBERS

object CommandChainValidator {

    private const val DEFAULT_PACKAGE_NAME = ""

    fun validateCommands(templateFragments: List<TemplateFragment>): List<TemplateRenderer> {
        val templateRendererKeywordCommand: KeywordCommand = assureFirstAndOnlyCommandIsTemplateDefinition(templateFragments)
        val templateModels = assureNoDuplicateModelNames(templateFragments)
        val templateClass = templateRendererKeywordCommand
            .toClassDescription(
                classNameAttribute = TEMPLATE_RENDERER_CLASS_NAME,
                packageNameAttribute = TEMPLATE_RENDERER_PACKAGE_NAME
            )

        val templateFragmentsToApply = mutableListOf<TemplateFragment>()

        val openingCommandKeysStack: MutableList<CommandKey> = mutableListOf()
        val remainingFragments = templateFragments
            .filterNot { it.isTemplateDefinitionCommand() }
            .filterNot { it.isModelDefinitionCommand() }
        remainingFragments.forEach { templateFragment ->
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

        val templateRenderer = TemplateRenderer(
            templateRendererClass = templateClass,
            modelClasses = templateModels,
            templateFragments = templateFragmentsToApply
        )
        return listOf(templateRenderer)

    }

    private fun assureNoDuplicateModelNames(
        templateFragments: List<TemplateFragment>
    ): List<ModelDescription> {

        val modelFragments = templateFragments
            .filterIsInstance<CommandFragment>()
            .filter { it.isModelDefinitionCommand() }

        val usedModelNames = mutableSetOf<String>()
        for(modelFragment in modelFragments) {
            val modelDescriptions = modelFragment.keywordCommand.toModelDescription()
            for(modelDescription in modelDescriptions) {
                if(modelDescription.modelName in usedModelNames) {
                    throw TemplateParsingException(
                        lineNumbers = modelFragment.lineNumbers,
                        msg = "The model name ${modelDescription.modelName} is used more than once."
                    )
                }
                usedModelNames.add(modelDescription.modelName)
            }
        }
        return modelFragments.flatMap { it.keywordCommand.toModelDescription() }
    }


    private fun assureFirstAndOnlyCommandIsTemplateDefinition(
        templateFragments: List<TemplateFragment>
    ): KeywordCommand {
        val count = templateFragments.count { it.isTemplateDefinitionCommand() }

        if(count != 1) {
            throw TemplateParsingException(
                lineNumbers = templateFragments.firstOrNull()?.lineNumbers ?: EMPTY_LINE_NUMBERS,
                msg = "There must be exactly one template command '${CommandKey.TEMPLATE_RENDERER.keyword}'. ",
            )
        }

        val commandFragment = templateFragments
            .filterIsInstance<CommandFragment>()
            .first()

        if(!commandFragment.isTemplateDefinitionCommand()) {
            throw TemplateParsingException(
                lineNumbers = commandFragment.lineNumbers,
                msg = "The first command in a file must be '${CommandKey.TEMPLATE_RENDERER.keyword}' " +
                        "but was ${commandFragment.keywordCommand.commandKey.keyword}. ",
            )
        }
        return commandFragment.keywordCommand
    }

    private fun KeywordCommand.toModelDescription(): List<ModelDescription> {
        return this.attributeGroupIndices().map { attributeGroupIndex ->
            ModelDescription(
                modelClassDescription = this.toClassDescription(
                    groupId = attributeGroupIndex,
                    classNameAttribute = TEMPLATE_MODEL_CLASS_NAME,
                    packageNameAttribute = TEMPLATE_MODEL_PACKAGE_NAME,
                ),
                modelName = this.attribute(
                    groupId = attributeGroupIndex,
                    key = TEMPLATE_MODEL_NAME,
                ),
            )
        }
    }

    private fun KeywordCommand.toClassDescription(
        groupId: Int = 0,
        classNameAttribute: CommandAttributeKey,
        packageNameAttribute: CommandAttributeKey,
    ): ClassDescription {
        return ClassDescription(
            className = this.attribute(groupId, classNameAttribute),
            classPackageName = this.attributeOptional(groupId, packageNameAttribute) ?: DEFAULT_PACKAGE_NAME,
        )
    }

    private fun TemplateFragment.isTemplateDefinitionCommand(): Boolean {
        return this is CommandFragment && this.keywordCommand.commandKey == CommandKey.TEMPLATE_RENDERER
    }

    private fun TemplateFragment.isModelDefinitionCommand(): Boolean {
        return this is CommandFragment && this.keywordCommand.commandKey == CommandKey.TEMPLATE_MODEL
    }

}
