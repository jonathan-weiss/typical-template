package org.codeblessing.typicaltemplate.contentparsing.commentparser

import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException

object TemplateCommentParser {
    private val attributeKeyPattern = Regex("""[a-zA-Z]+""")
    private val attributeValuePattern = Regex("""[^"]*""")

    private val attributePairPattern = Regex("""${attributeKeyPattern}="$attributeValuePattern"""")

    private val attributePairsGroupingPattern = Regex("""(${attributeKeyPattern})="($attributeValuePattern)"""")
    private val keywordPattern = Regex("""[a-z][a-z\\-]+""")

    private val commentValidationPattern = Regex("""^\s*@@tt-(${keywordPattern})\s*((?:\[(?:\s*$attributePairPattern\s+)*\s*$attributePairPattern\s*]\s*)*)$""", RegexOption.MULTILINE)
    private val bracketsGroupingPattern = Regex("""\[((?:\s*$attributePairPattern\s*)*)]\s*""", RegexOption.MULTILINE)


    fun parseComment(comment: String): TemplateComment {
        if(!commentValidationPattern.matches(comment)) {
            throw TemplateParsingException(
                msg = "Invalid comment structure. " +
                        "Content of comment must be (without the < and > characters): " +
                        "@@tt-<keyword>[<attribute1>=\"<value1>\" <attribute2>=\"<value2>\"][<attribute1>=\"<value3>\"]"
            )
        }

        val match = requireNotNull(commentValidationPattern.find(comment))
        val keyword = match.groupValues[1]
        val bracketsString = match.groupValues[2]

        val bracketsContent = bracketsGroupingPattern
            .findAll(bracketsString)
            .map { it.groupValues[1].trim() }
            .toList()

        return TemplateComment(
            keyword = keyword,
            brackets = bracketsContent
                .map { parseBracketContent(it) }
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
