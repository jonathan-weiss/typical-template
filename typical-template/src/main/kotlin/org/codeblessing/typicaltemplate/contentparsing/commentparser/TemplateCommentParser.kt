package org.codeblessing.typicaltemplate.contentparsing.commentparser

import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException

/**
 * Transform a template comment string (a list of keywords with attributes) to its structure.
 * A valid structure is something like one or many of
 * @<keyword>[<attribute1>="<value1>" <attribute2>="<value2>"][<attribute1>="<value3>"]
 * or using '#' as the prefix instead of '@'.
 */
object TemplateCommentParser {

    private val attributeKeyPattern = Regex("""[a-zA-Z]+""")
    private val attributeValuePattern = Regex("""[^"]*""")

    private val attributePairPattern = Regex("""${attributeKeyPattern}="$attributeValuePattern"""")

    private val attributePairsGroupingPattern = Regex("""(${attributeKeyPattern})="($attributeValuePattern)"""")
    private val keywordPattern = Regex("""[a-z][a-z\\-]+""")

    private val singleCommandKeywordAndAttributesGroupingPattern = Regex("""\s*[@#](${keywordPattern})\s*((?:\[(?:\s*$attributePairPattern\s+)*\s*$attributePairPattern\s*]\s*)*)""", RegexOption.MULTILINE)
    private val bracketsGroupingPattern = Regex("""\[((?:\s*$attributePairPattern\s*)*)]\s*""", RegexOption.MULTILINE)

    private val multiCommandValidationPattern = Regex("""\s*(${singleCommandKeywordAndAttributesGroupingPattern.pattern}\s*)+\s*""", RegexOption.MULTILINE)

    fun parseComment(comment: String): List<StructuredKeyword> {
        if(!multiCommandValidationPattern.matches(comment)) {
            throw TemplateParsingException(
                msg = "Invalid comment structure. " +
                        "Content of comment must be one or many commands of this structure (without the < and > characters): " +
                        "@<keyword>[<attribute1>=\"<value1>\" <attribute2>=\"<value2>\"][<attribute3>=\"<value3>\"] " +
                        "(use '@' or '#' as the keyword prefix)"
            )
        }

        return singleCommandKeywordAndAttributesGroupingPattern
            .findAll(comment)
            .map { match -> match.value }
            .map { command -> parseSingleCommand(command) }
            .toList()
    }

    private fun parseSingleCommand(command: String): StructuredKeyword {
        val match = requireNotNull(singleCommandKeywordAndAttributesGroupingPattern.find(command))
        val keyword = match.groupValues[1]
        val bracketsString = match.groupValues[2]
        val keywordType = if (match.value.trimStart().startsWith("#")) KeywordType.PREPROCESSING else KeywordType.COMMAND

        val bracketsContent = bracketsGroupingPattern
            .findAll(bracketsString)
            .map { it.groupValues[1].trim() }
            .toList()

        return StructuredKeyword(
            keyword = keyword,
            brackets = bracketsContent
                .map { parseBracketContent(it) },
            keywordType = keywordType,
        )
    }

    private fun parseBracketContent(bracketContent: String): Map<String, String> {
        val keyValuePairs = mutableMapOf<String, String>()

        val attributePairMatches = attributePairsGroupingPattern.findAll(bracketContent)
        for (attributePairMatch in attributePairMatches) {
            val attributeName = attributePairMatch.groupValues[1]
            val attributeValue = attributePairMatch.groupValues[2]

            if(attributeName.isEmpty()) {
                throw TemplateParsingException(msg = "Key can not be empty.")
            }
            if(attributeName in keyValuePairs) {
                throw TemplateParsingException(msg = "Duplicate use of '$attributeName'.")
            }
            keyValuePairs[attributeName] = attributeValue
        }
        
        return keyValuePairs
    }
}
