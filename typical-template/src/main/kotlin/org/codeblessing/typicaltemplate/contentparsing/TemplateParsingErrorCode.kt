package org.codeblessing.typicaltemplate.contentparsing

enum class TemplateParsingErrorCode(val messageTemplate: String) {
    INVALID_JAVA_CLASS_NAME(
        "The value '{value}' for attribute '{attributeKey}' in command '{command}' is not a valid Java class name."
    ),
    INVALID_JAVA_PACKAGE_NAME(
        "The value '{value}' for attribute '{attributeKey}' in command '{command}' is not a valid Java package name."
    ),
    INVALID_JAVA_PARAMETER_NAME(
        "The value '{value}' for attribute '{attributeKey}' in command '{command}' is not a valid Java parameter name."
    ),
    DUPLICATE_MODEL_NAME(
        "The model name '{modelName}' is used more than once in command '{command}'. Model names must be unique."
    ),
    UNCLOSED_OPENING_COMMAND(
        "The opening command '{openingCommand}' is missing its closing command '{closingCommand}'."
    ),
    MISMATCHED_CLOSING_COMMAND(
        "The closing command '{closingCommand}' has no corresponding opening command '{openingCommand}' on the same nesting level."
    ),
    COMMAND_NOT_DIRECTLY_NESTED(
        "The command '{command}' must be directly nested inside '{enclosingCommand}'."
    ),
    ERROR_SPLITTING_TEMPLATE(
        "Error splitting template content: {message}"
    ),
    TEMPLATE_RENDERER_BLOCK_NOT_CLOSED(
        "TEMPLATE_RENDERER block is not closed with END_TEMPLATE_RENDERER"
    ),
    INVALID_COMMENT_STRUCTURE(
        "Invalid comment structure. " +
        "Content of comment must be one or many commands of this structure (without the < and > characters): " +
        "@<keyword>[<attribute1>=\"<value1>\" <attribute2>=\"<value2>\"][<attribute3>=\"<value3>\"]"
    ),
    EMPTY_ATTRIBUTE_KEY(
        "Key can not be empty."
    ),
    DUPLICATE_ATTRIBUTE_KEY(
        "Duplicate use of '{attributeName}'."
    ),
    SEARCH_TOKEN_NOT_FOUND(
        "Occurrence of '{searchToken}' not found in text"
    ),
    MULTIPLE_MOVE_COMMENT_COMMANDS(
        "A template comment must not have more than one move-comment command, but found {count}."
    ),
    MULTIPLE_EXPAND_COMMENT_COMMANDS(
        "A template comment must not have more than one '{command}' command with direction '{direction}', but found {count}."
    ),
    UNKNOWN_KEYWORD(
        "Invalid keyword '{keyword}'."
    ),
    TOO_FEW_ATTRIBUTE_GROUPS(
        "Invalid number of attributes groups. Must be at least {min} but was {actual}."
    ),
    TOO_MANY_ATTRIBUTE_GROUPS(
        "Invalid number of attributes groups. Only {max} are allowed but was {actual}."
    ),
    UNKNOWN_ATTRIBUTE_KEY(
        "Unknown attribute key '{key}' in attributes group #{groupIndex}. Only the following attributes are allowed: {allowedAttributes}."
    ),
    ATTRIBUTE_KEY_NOT_ALLOWED(
        "Not allowed attribute key '{key}' in attributes group #{groupIndex}. Only the following attributes are allowed: {allowedAttributes}."
    ),
    ATTRIBUTE_VALUE_NOT_ALLOWED(
        "Not allowed attribute value '{value}' for key '{key}' in attributes group #{groupIndex}. Only the following attributes are allowed: {allowedValues}."
    ),
    BLANK_ATTRIBUTE_VALUE(
        "The attribute value for key '{key}' in attributes group #{groupIndex} must not be blank."
    ),
    MISSING_REQUIRED_ATTRIBUTES(
        "Not all required attributes are present for command '{command}'. The following attributes are missing in attributes group #{groupIndex}: {missingAttributes} "
    ),
    UNALLOWED_ATTRIBUTES(
        "The following attributes are not allowed for command '{command}' in attributes group #{groupIndex}: {unallowedAttributes} "
    ),
    MUTUALLY_EXCLUSIVE_ATTRIBUTES(
        "Only one of the following mutually exclusive attributes may be present for command '{command}' in attributes group #{groupIndex}: {mutualExclusiveAttributes}. Found: {foundAttributes}."
    );

    fun resolve(vararg pairs: Pair<String, String>): String {
        val resolved = pairs.fold(messageTemplate) { msg, (key, value) -> msg.replace("{$key}", value) }
        checkHasNotUnfilledPlaceholders(resolved)
        return resolved
    }

    private fun checkHasNotUnfilledPlaceholders(resolved: String) {
        val unfilled = Regex("""\{[^}]+}""").findAll(resolved).map { it.value }.toList()
        if (unfilled.isNotEmpty()) {
            throw RuntimeException("Unresolved placeholder(s) $unfilled in error code $name")
        }
    }
}
