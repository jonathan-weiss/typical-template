package org.codeblessing.typicaltemplate.contentparsing.commentparser

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
            val input = "${keywordPrefix}my-keyword"
            val comment = parseCommentExpectingSingeResult(input)
            assertEquals("my-keyword", comment.keyword)
            assertTrue(comment.brackets.isEmpty())
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles command with single bracket containing key-value`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="bar"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(1, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles command with single bracket containing multiple key-value`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="bar" far="baz"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(1, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("baz", comment.brackets[0]["far"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles command with multiple brackets`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="bar"][fox="bar2"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("bar2", comment.brackets[1]["fox"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles command with multiple brackets containing multiple key-value pairs`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="bar" far="baz"][fox="bar2" far="baz"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("baz", comment.brackets[0]["far"])
            assertEquals("bar2", comment.brackets[1]["fox"])
            assertEquals("baz", comment.brackets[1]["far"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace before at-sign`(keywordPrefix: String) {
            val input = """   ${keywordPrefix}my-keyword[foo="bar"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("bar", comment.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace after keyword`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword  [foo="bar"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("bar", comment.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace after command`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="bar"]   """
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("bar", comment.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles special characters in value`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="ba,  $#@+^d\*pi&/-vr"]   """
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("""ba,  $#@+^d\*pi&/-vr""", comment.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles opening bracket in value`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="ba[r"]   """
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("ba[r", comment.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles closing bracket in value`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="ba]r"]   """
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("ba]r", comment.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles line breaks`(keywordPrefix: String) {
            val input = """

             ${keywordPrefix}my-keyword[foo="bar"]

             """.trimIndent()
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("bar", comment.brackets[0]["foo"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace in brackets`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword [ foo="bar" ][ fox="bar2" ]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("bar2", comment.brackets[1]["fox"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace in brackets with multiple key-value pairs`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword [ foo="bar"   far="baz"  ][ fox="bar2" ]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("bar2", comment.brackets[1]["fox"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles whitespace between brackets`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword [foo="bar"] [fox="bar2"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("bar2", comment.brackets[1]["fox"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles complex whitespace scenario`(keywordPrefix: String) {
            val input = """


              ${keywordPrefix}my-keyword  [  foo="bar"  ]  [  fox="bar2 bar2"  ]

                """.trimIndent()
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("bar2 bar2", comment.brackets[1]["fox"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment handles complex newline scenario`(keywordPrefix: String) {
            val input = """


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
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("gain", comment.brackets[0]["fee"])
            assertEquals("trot", comment.brackets[0]["fox"])
            assertEquals("grid", comment.brackets[1]["flex"])
            assertEquals("fit", comment.brackets[1]["fox"])
            assertEquals("fox", comment.brackets[1]["fix"])
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment sets keywordType based on prefix`(keywordPrefix: String) {
            val input = "${keywordPrefix}my-keyword"
            val comment = parseCommentExpectingSingeResult(input)
            val expectedKeywordType = if (keywordPrefix == "#") {
                KeywordType.PREPROCESSING
            } else {
                KeywordType.COMMAND
            }
            assertEquals(expectedKeywordType, comment.keywordType)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for empty string`(keywordPrefix: String) {
            val input = ""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception with empty brackets`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception with multiple empty brackets`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[][foo="bar"][]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for duplicate keys`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="bar" foo="bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for whitespaces between key and equal sign`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo  ="bar"][fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for whitespaces between equal sign and value`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo=  "bar"][fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for whitespaces between key and value`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo  =  "bar"][fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for key-value pairs with comma as separator`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="bar",far="baz"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for key-value pairs missing a space as separator`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="bar"far="baz"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for unquoted values`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo=bar]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for value with quote character`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="ba"r"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for key-value pairs with missing quote at start`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo=bar"][fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for key-value pairs with missing quote at the end`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="bar][fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for missing closing bracket`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword[foo="bar"[fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for missing opening bracket`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword [foo="bar"]fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `parseComment throws exception for attribute key with special characters`(keywordPrefix: String) {
            val input = """${keywordPrefix}my-keyword [foo*="bar"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        fun parseCommentExpectingSingeResult(comment: String): StructuredKeyword {
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
            val comment = "${keywordPrefix}command-name"
            val structuredKeywords = TemplateCommentParser.parseComment(comment)
            assertEquals(1, structuredKeywords.size)
            assertEquals("command-name", structuredKeywords.first().keyword)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `a comment with one command having one attribute group is not splitted`(keywordPrefix: String) {
            val comment = "${keywordPrefix}command-name ${commandGroup()}"
            val structuredKeywords = TemplateCommentParser.parseComment(comment)
            assertEquals(1, structuredKeywords.size)
            assertEquals("command-name", structuredKeywords.first().keyword)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `a comment with one command having multiple attribute groups is not splitted`(keywordPrefix: String) {
            val comment = "${keywordPrefix}command-name ${commandGroup()}${commandGroup()}${commandGroup()}"
            val structuredKeywords = TemplateCommentParser.parseComment(comment)
            assertEquals(1, structuredKeywords.size)
            assertEquals("command-name", structuredKeywords.first().keyword)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `a comment with two command having no attribute group is splitted into two commands`(keywordPrefix: String) {
            val comment = "${keywordPrefix}command-name-a ${keywordPrefix}command-name-b"
            val structuredKeywords = TemplateCommentParser.parseComment(comment)
            assertEquals(2, structuredKeywords.size)
            assertEquals("command-name-a", structuredKeywords.first().keyword)
            assertEquals("command-name-b", structuredKeywords.last().keyword)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `a comment with two commands having each one attribute group is splitted into two commands`(keywordPrefix: String) {
            val comment = "${keywordPrefix}command-name-a ${commandGroup()} ${keywordPrefix}command-name-b ${commandGroup()}"
            val structuredKeywords = TemplateCommentParser.parseComment(comment)
            assertEquals(2, structuredKeywords.size)
            assertEquals("command-name-a", structuredKeywords.first().keyword)
            assertEquals("command-name-b", structuredKeywords.last().keyword)
        }

        @ParameterizedTest(name = "prefix=''{0}''")
        @ValueSource(strings = ["@", "#"])
        fun `a comment with two commands having multiple attribute groups is splitted into two commands`(keywordPrefix: String) {
            val comment = "${keywordPrefix}command-name-a \t ${commandGroup()} \n\t ${commandGroup()}${commandGroup()} ${keywordPrefix}command-name-b \n\n\t ${commandGroup()}\n ${commandGroup()}\n\t  ${commandGroup()}"
            val structuredKeywords = TemplateCommentParser.parseComment(comment)
            assertEquals(2, structuredKeywords.size)
            assertEquals("command-name-a", structuredKeywords.first().keyword)
            assertEquals("command-name-b", structuredKeywords.last().keyword)
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
