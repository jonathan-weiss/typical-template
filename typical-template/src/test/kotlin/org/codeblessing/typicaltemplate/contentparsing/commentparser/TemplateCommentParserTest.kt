package org.codeblessing.typicaltemplate.contentparsing.commentparser

import org.codeblessing.typicaltemplate.contentparsing.TemplateParsingException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TemplateCommentParserTest {
    
    @Nested
    inner class SingleCommandComment {

        @Test
        fun `parseComment handles basic command without brackets`() {
            val input = "@my-keyword"
            val comment = parseCommentExpectingSingeResult(input)
            assertEquals("my-keyword", comment.keyword)
            assertTrue(comment.brackets.isEmpty())
        }

        @Test
        fun `parseComment handles command with single bracket containing key-value`() {
            val input = """@my-keyword[foo="bar"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(1, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
        }

        @Test
        fun `parseComment handles command with single bracket containing multiple key-value`() {
            val input = """@my-keyword[foo="bar" far="baz"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(1, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("baz", comment.brackets[0]["far"])
        }

        @Test
        fun `parseComment handles command with multiple brackets`() {
            val input = """@my-keyword[foo="bar"][fox="bar2"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("bar2", comment.brackets[1]["fox"])
        }

        @Test
        fun `parseComment handles command with multiple brackets containing multiple key-value pairs`() {
            val input = """@my-keyword[foo="bar" far="baz"][fox="bar2" far="baz"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("baz", comment.brackets[0]["far"])
            assertEquals("bar2", comment.brackets[1]["fox"])
            assertEquals("baz", comment.brackets[1]["far"])
        }

        @Test
        fun `parseComment handles whitespace before at-sign`() {
            val input = """   @my-keyword[foo="bar"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("bar", comment.brackets[0]["foo"])
        }

        @Test
        fun `parseComment handles whitespace after keyword`() {
            val input = """@my-keyword  [foo="bar"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("bar", comment.brackets[0]["foo"])
        }

        @Test
        fun `parseComment handles whitespace after command`() {
            val input = """@my-keyword[foo="bar"]   """
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("bar", comment.brackets[0]["foo"])
        }

        @Test
        fun `parseComment handles special characters in value`() {
            val input = """@my-keyword[foo="ba,  $#@+^d\*pi&/-vr"]   """
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("""ba,  $#@+^d\*pi&/-vr""", comment.brackets[0]["foo"])
        }

        @Test
        fun `parseComment handles opening bracket in value`() {
            val input = """@my-keyword[foo="ba[r"]   """
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("ba[r", comment.brackets[0]["foo"])
        }

        @Test
        fun `parseComment handles closing bracket in value`() {
            val input = """@my-keyword[foo="ba]r"]   """
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("ba]r", comment.brackets[0]["foo"])
        }

        @Test
        fun `parseComment handles line breaks`() {
            val input = """
             
             @my-keyword[foo="bar"]
             
             """.trimIndent()
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals("bar", comment.brackets[0]["foo"])
        }

        @Test
        fun `parseComment handles whitespace in brackets`() {
            val input = """@my-keyword [ foo="bar" ][ fox="bar2" ]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("bar2", comment.brackets[1]["fox"])
        }

        @Test
        fun `parseComment handles whitespace in brackets with multiple key-value pairs`() {
            val input = """@my-keyword [ foo="bar"   far="baz"  ][ fox="bar2" ]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("bar2", comment.brackets[1]["fox"])
        }

        @Test
        fun `parseComment handles whitespace between brackets`() {
            val input = """@my-keyword [foo="bar"] [fox="bar2"]"""
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("bar2", comment.brackets[1]["fox"])
        }

        @Test
        fun `parseComment handles complex whitespace scenario`() {
            val input = """  
              
              
              @my-keyword  [  foo="bar"  ]  [  fox="bar2 bar2"  ]  
                
                """.trimIndent()
            val comment = parseCommentExpectingSingeResult(input)

            assertEquals("my-keyword", comment.keyword)
            assertEquals(2, comment.brackets.size)
            assertEquals("bar", comment.brackets[0]["foo"])
            assertEquals("bar2 bar2", comment.brackets[1]["fox"])
        }

        @Test
        fun `parseComment handles complex newline scenario`() {
            val input = """  
              
              
              @my-keyword  [  
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

        @Test
        fun `parseComment throws exception for empty string`() {
            val input = ""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception with empty brackets`() {
            val input = """@my-keyword[]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception with multiple empty brackets`() {
            val input = """@my-keyword[][foo="bar"][]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for duplicate keys`() {
            val input = """@my-keyword[foo="bar" foo="bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for whitespaces between key and equal sign`() {
            val input = """@my-keyword[foo  ="bar"][fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for whitespaces between equal sign and value`() {
            val input = """@my-keyword[foo=  "bar"][fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for whitespaces between key and value`() {
            val input = """@my-keyword[foo  =  "bar"][fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for key-value pairs with comma as separator`() {
            val input = """@my-keyword[foo="bar",far="baz"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for key-value pairs missing a space as separator`() {
            val input = """@my-keyword[foo="bar"far="baz"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for unquoted values`() {
            val input = """@my-keyword[foo=bar]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for value with quote character`() {
            val input = """@my-keyword[foo="ba"r"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for key-value pairs with missing quote at start`() {
            val input = """@my-keyword[foo=bar"][fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for key-value pairs with missing quote at the end`() {
            val input = """@my-keyword[foo="bar][fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for missing closing bracket`() {
            val input = """@my-keyword[foo="bar"[fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for missing opening bracket`() {
            val input = """@my-keyword [foo="bar"]fox="bar2 bar2"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        @Test
        fun `parseComment throws exception for attribute key with special characters`() {
            val input = """@my-keyword [foo*="bar"]"""
            assertThrows<TemplateParsingException> {
                TemplateCommentParser.parseComment(input)
            }
        }

        fun parseCommentExpectingSingeResult(comment: String): StructuredComment {
            val commands = TemplateCommentParser.parseComment(comment)
            assertEquals(1, commands.size, "Expected comment to have exactly one command")
            return commands.single()
        }
    }
    
    @Nested
    inner class MultipleCommandsComment {

        @Test
        fun `a comment with one command having no attribute group is not splitted`() {
            val comment = "@command-name"
            val structuredComments = TemplateCommentParser.parseComment(comment)
            assertEquals(1, structuredComments.size)
            assertEquals("command-name", structuredComments.first().keyword)
        }

        @Test
        fun `a comment with one command having one attribute group is not splitted`() {
            val comment = "@command-name ${commandGroup()}"
            val structuredComments = TemplateCommentParser.parseComment(comment)
            assertEquals(1, structuredComments.size)
            assertEquals("command-name", structuredComments.first().keyword)
        }

        @Test
        fun `a comment with one command having multiple attribute groups is not splitted`() {
            val comment = "@command-name ${commandGroup()}${commandGroup()}${commandGroup()}"
            val structuredComments = TemplateCommentParser.parseComment(comment)
            assertEquals(1, structuredComments.size)
            assertEquals("command-name", structuredComments.first().keyword)
        }

        @Test
        fun     `a comment with two command having no attribute group is splitted into two commands`() {
            val comment = "@command-name-a @command-name-b"
            val structuredComments = TemplateCommentParser.parseComment(comment)
            assertEquals(2, structuredComments.size)
            assertEquals("command-name-a", structuredComments.first().keyword)
            assertEquals("command-name-b", structuredComments.last().keyword)
        }

        @Test
        fun `a comment with two commands having each one attribute group is splitted into two commands`() {
            val comment = "@command-name-a ${commandGroup()} @command-name-b ${commandGroup()}"
            val structuredComments = TemplateCommentParser.parseComment(comment)
            assertEquals(2, structuredComments.size)
            assertEquals("command-name-a", structuredComments.first().keyword)
            assertEquals("command-name-b", structuredComments.last().keyword)
        }

        @Test
        fun `a comment with two commands having multiple attribute groups is splitted into two commands`() {
            val comment = "@command-name-a \t ${commandGroup()} \n\t ${commandGroup()}${commandGroup()} @command-name-b \n\n\t ${commandGroup()}\n ${commandGroup()}\n\t  ${commandGroup()}"
            val structuredComments = TemplateCommentParser.parseComment(comment)
            assertEquals(2, structuredComments.size)
            assertEquals("command-name-a", structuredComments.first().keyword)
            assertEquals("command-name-b", structuredComments.last().keyword)
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
