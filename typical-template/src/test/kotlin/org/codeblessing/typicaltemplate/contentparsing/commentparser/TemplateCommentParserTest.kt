package org.codeblessing.typicaltemplate.contentparsing.commentparser

import org.codeblessing.typicaltemplate.KeywordType
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingErrorCode
import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class TemplateCommentParserTest {

    @Nested
    inner class SingleCommandComment {

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles basic command without brackets`(keywordPrefix: String) {
            val commentText = "${keywordPrefix}my-keyword"
            val commandStructure = parseCommentExpectingSingeResult(commentText)
            assertEquals("my-keyword", commandStructure.keyword)
            assertTrue(commandStructure.brackets.isEmpty())
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles command with single bracket containing key-value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar"]"""
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals(1, commandStructure.brackets.size)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles command with single bracket containing multiple key-value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar" far="baz"]"""
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals(1, commandStructure.brackets.size)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
            assertEquals("baz", commandStructure.brackets[0]["far"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles command with multiple brackets`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar"][fox="bar2"]"""
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals(2, commandStructure.brackets.size)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
            assertEquals("bar2", commandStructure.brackets[1]["fox"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles command with multiple brackets containing multiple key-value pairs`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar" far="baz"][fox="bar2" far="baz"]"""
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals(2, commandStructure.brackets.size)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
            assertEquals("baz", commandStructure.brackets[0]["far"])
            assertEquals("bar2", commandStructure.brackets[1]["fox"])
            assertEquals("baz", commandStructure.brackets[1]["far"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace before at-sign`(keywordPrefix: String) {
            val commentText = """   ${keywordPrefix}my-keyword[foo="bar"]"""
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace after keyword`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword  [foo="bar"]"""
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace after command`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar"]   """
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles special characters in value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="ba,  $#@+^d\*pi&/-vr"]   """
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("""ba,  $#@+^d\*pi&/-vr""", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles opening bracket in value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="ba[r"]   """
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("ba[r", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles closing bracket in value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="ba]r"]   """
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("ba]r", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles line breaks`(keywordPrefix: String) {
            val commentText = """

             ${keywordPrefix}my-keyword[foo="bar"]

             """.trimIndent()
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace in brackets`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword [ foo="bar" ][ fox="bar2" ]"""
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals(2, commandStructure.brackets.size)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
            assertEquals("bar2", commandStructure.brackets[1]["fox"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace in brackets with multiple key-value pairs`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword [ foo="bar"   far="baz"  ][ fox="bar2" ]"""
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals(2, commandStructure.brackets.size)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
            assertEquals("bar2", commandStructure.brackets[1]["fox"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace between brackets`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword [foo="bar"] [fox="bar2"]"""
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals(2, commandStructure.brackets.size)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
            assertEquals("bar2", commandStructure.brackets[1]["fox"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles complex whitespace scenario`(keywordPrefix: String) {
            val commentText = """


              ${keywordPrefix}my-keyword  [  foo="bar"  ]  [  fox="bar2 bar2"  ]

                """.trimIndent()
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals(2, commandStructure.brackets.size)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
            assertEquals("bar2 bar2", commandStructure.brackets[1]["fox"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles complex newline scenario`(keywordPrefix: String) {
            val commentText = """


              ${keywordPrefix}my-keyword  [
                foo="bar"
                fee="gain"
                fox="trot"
              ]
              [
                flex="grid" fox="fit"
                fix="fox"
              ]

                """.trimIndent()
            val commandStructure = parseCommentExpectingSingeResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals(2, commandStructure.brackets.size)
            assertEquals("bar", commandStructure.brackets[0]["foo"])
            assertEquals("gain", commandStructure.brackets[0]["fee"])
            assertEquals("trot", commandStructure.brackets[0]["fox"])
            assertEquals("grid", commandStructure.brackets[1]["flex"])
            assertEquals("fit", commandStructure.brackets[1]["fox"])
            assertEquals("fox", commandStructure.brackets[1]["fix"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment sets keywordType based on prefix`(keywordPrefix: String) {
            val commentText = "${keywordPrefix}my-keyword"
            val commandStructure = parseCommentExpectingSingeResult(commentText)
            val expectedKeywordType = if (keywordPrefix == "#") {
                KeywordType.PREPROCESSOR_COMMAND
            } else {
                KeywordType.COMMAND
            }
            assertEquals(expectedKeywordType, commandStructure.keywordType)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for empty string`(keywordPrefix: String) {
            val commentText = ""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception with empty brackets`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception with multiple empty brackets`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[][foo="bar"][]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for duplicate keys`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar" foo="bar2"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.DUPLICATE_ATTRIBUTE_KEY, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for whitespaces between key and equal sign`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo  ="bar"][fox="bar2 bar2"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for whitespaces between equal sign and value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo=  "bar"][fox="bar2 bar2"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for whitespaces between key and value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo  =  "bar"][fox="bar2 bar2"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for key-value pairs with comma as separator`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar",far="baz"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for key-value pairs missing a space as separator`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar"far="baz"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for unquoted values`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo=bar]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for value with quote character`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="ba"r"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for key-value pairs with missing quote at start`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo=bar"][fox="bar2 bar2"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for key-value pairs with missing quote at the end`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar][fox="bar2 bar2"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for missing closing bracket`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar"[fox="bar2 bar2"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for missing opening bracket`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword [foo="bar"]fox="bar2 bar2"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for attribute key with special characters`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword [foo*="bar"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        fun parseCommentExpectingSingeResult(comment: String): CommandStructure {
            val commands = TemplateCommentParser.parseComment(comment)
            assertEquals(1, commands.size, "Expected comment to have exactly one command")
            return commands.single()
        }
    }

    @Nested
    inner class EscapingAndDecoding {

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment decodes escaped double quote in value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="ba\"r"]"""
            val commandStructure = parseCommentExpectingSingleResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("""ba"r""", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment decodes escaped double quote as entire value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="\""]"""
            val commandStructure = parseCommentExpectingSingleResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("\"", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment decodes escaped backslash in value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="ba\\r"]"""
            val commandStructure = parseCommentExpectingSingleResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("""ba\r""", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment decodes escaped backslash at end of value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar\\"]"""
            val commandStructure = parseCommentExpectingSingleResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("""bar\""", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment decodes multiple escaped characters in value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="a\"b\\c\"d"]"""
            val commandStructure = parseCommentExpectingSingleResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("""a"b\c"d""", commandStructure.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment decodes escaped characters across multiple key-value pairs`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="say \"hello\"" bar="path\\to\\file"]"""
            val commandStructure = parseCommentExpectingSingleResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals("""say "hello"""", commandStructure.brackets[0]["foo"])
            assertEquals("""path\to\file""", commandStructure.brackets[0]["bar"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment decodes escaped characters across multiple brackets`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="\"quoted\""][bar="back\\slash"]"""
            val commandStructure = parseCommentExpectingSingleResult(commentText)

            assertEquals("my-keyword", commandStructure.keyword)
            assertEquals(""""quoted"""", commandStructure.brackets[0]["foo"])
            assertEquals("""back\slash""", commandStructure.brackets[1]["bar"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for unescaped double quote inside value`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="ba"r"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for trailing backslash that is not an escape sequence`(keywordPrefix: String) {
            val commentText = """${keywordPrefix}my-keyword[foo="bar\"]"""
            val exception = assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(commentText)
            }
            assertEquals(TemplateParsingErrorCode.INVALID_COMMENT_STRUCTURE, exception.errorCode)
        }

        private fun parseCommentExpectingSingleResult(comment: String): CommandStructure {
            val commands = TemplateCommentParser.parseComment(comment)
            assertEquals(1, commands.size, "Expected comment to have exactly one command")
            return commands.single()
        }
    }

    @Nested
    inner class MultipleCommandsComment {

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `a comment with one command having no attribute group is not splitted`(keywordPrefix: String) {
            val commentText = "${keywordPrefix}command-name"
            val commandStructureList = TemplateCommentParser.parseComment(commentText)
            assertEquals(1, commandStructureList.size)
            assertEquals("command-name", commandStructureList.first().keyword)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `a comment with one command having one attribute group is not splitted`(keywordPrefix: String) {
            val commentText = "${keywordPrefix}command-name ${commandGroup()}"
            val commandStructureList = TemplateCommentParser.parseComment(commentText)
            assertEquals(1, commandStructureList.size)
            assertEquals("command-name", commandStructureList.first().keyword)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `a comment with one command having multiple attribute groups is not splitted`(keywordPrefix: String) {
            val commentText = "${keywordPrefix}command-name ${commandGroup()}${commandGroup()}${commandGroup()}"
            val commandStructureList = TemplateCommentParser.parseComment(commentText)
            assertEquals(1, commandStructureList.size)
            assertEquals("command-name", commandStructureList.first().keyword)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `a comment with two command having no attribute group is splitted into two commands`(keywordPrefix: String) {
            val commentText = "${keywordPrefix}command-name-a ${keywordPrefix}command-name-b"
            val commandStructureList = TemplateCommentParser.parseComment(commentText)
            assertEquals(2, commandStructureList.size)
            assertEquals("command-name-a", commandStructureList.first().keyword)
            assertEquals("command-name-b", commandStructureList.last().keyword)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `a comment with two commands having each one attribute group is splitted into two commands`(keywordPrefix: String) {
            val commentText = "${keywordPrefix}command-name-a ${commandGroup()} ${keywordPrefix}command-name-b ${commandGroup()}"
            val commandStructureList = TemplateCommentParser.parseComment(commentText)
            assertEquals(2, commandStructureList.size)
            assertEquals("command-name-a", commandStructureList.first().keyword)
            assertEquals("command-name-b", commandStructureList.last().keyword)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `a comment with two commands having multiple attribute groups is splitted into two commands`(keywordPrefix: String) {
            val commentText = "${keywordPrefix}command-name-a \t ${commandGroup()} \n\t ${commandGroup()}${commandGroup()} ${keywordPrefix}command-name-b \n\n\t ${commandGroup()}\n ${commandGroup()}\n\t  ${commandGroup()}"
            val commandStructureList = TemplateCommentParser.parseComment(commentText)
            assertEquals(2, commandStructureList.size)
            assertEquals("command-name-a", commandStructureList.first().keyword)
            assertEquals("command-name-b", commandStructureList.last().keyword)
        }

        private fun commandGroup(): String {
            return "[\n" +
                    "attributeA=\"value1\"\n" +
                    "attributeB=\"value2\"\n" +
                    "attributeC=\"value3\"\n" +
                    "]"
        }
    }
}
