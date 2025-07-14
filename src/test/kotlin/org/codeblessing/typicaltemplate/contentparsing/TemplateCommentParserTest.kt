package org.codeblessing.typicaltemplate.contentparsing

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TemplateCommentParserTest {
    
    @Test
    fun `parseComment handles basic command without brackets`() {
        val input = "@@tt-template"
        val comment = TemplateCommentParser.parseComment(input)
        assertEquals("template", comment.keyword)
        assertTrue(comment.brackets.isEmpty())
    }

    @Test
    fun `parseComment handles command with single bracket containing key-value`() {
        val input = """@@tt-template[foo="bar"]"""
        val comment = TemplateCommentParser.parseComment(input)
        
        assertEquals("template", comment.keyword)
        assertEquals(1, comment.brackets.size)
        assertEquals("bar", comment.brackets[0]["foo"])
    }
    
    @Test
    fun `parseComment handles command with single bracket containing multiple key-value`() {
        val input = """@@tt-template[foo="bar" far="baz"]"""
        val comment = TemplateCommentParser.parseComment(input)

        assertEquals("template", comment.keyword)
        assertEquals(1, comment.brackets.size)
        assertEquals("bar", comment.brackets[0]["foo"])
        assertEquals("baz", comment.brackets[0]["far"])
    }

    @Test
    fun `parseComment handles command with multiple brackets`() {
        val input = """@@tt-template[foo="bar"][fox="bar2"]"""
        val comment = TemplateCommentParser.parseComment(input)
        
        assertEquals("template", comment.keyword)
        assertEquals(2, comment.brackets.size)
        assertEquals("bar", comment.brackets[0]["foo"])
        assertEquals("bar2", comment.brackets[1]["fox"])
    }
    
    @Test
    fun `parseComment handles command with multiple brackets containing multiple key-value pairs`() {
        val input = """@@tt-template[foo="bar" far="baz"][fox="bar2" far="baz"]"""
        val comment = TemplateCommentParser.parseComment(input)

        assertEquals("template", comment.keyword)
        assertEquals(2, comment.brackets.size)
        assertEquals("bar", comment.brackets[0]["foo"])
        assertEquals("baz", comment.brackets[0]["far"])
        assertEquals("bar2", comment.brackets[1]["fox"])
        assertEquals("baz", comment.brackets[1]["far"])
    }

    @Test
    fun `parseComment handles whitespace before tt-`() {
        val input = """   @@tt-template[foo="bar"]"""
        val comment = TemplateCommentParser.parseComment(input)
        
        assertEquals("template", comment.keyword)
        assertEquals("bar", comment.brackets[0]["foo"])
    }
    
    @Test
    fun `parseComment handles whitespace after keyword`() {
        val input = """@@tt-template  [foo="bar"]"""
        val comment = TemplateCommentParser.parseComment(input)

        assertEquals("template", comment.keyword)
        assertEquals("bar", comment.brackets[0]["foo"])
    }

    @Test
    fun `parseComment handles whitespace after command`() {
        val input = """@@tt-template[foo="bar"]   """
        val comment = TemplateCommentParser.parseComment(input)
        
        assertEquals("template", comment.keyword)
        assertEquals("bar", comment.brackets[0]["foo"])
    }

    @Test
    fun `parseComment handles special characters in value`() {
        val input = """@@tt-template[foo="ba,  $#@+^d\*pi&/-vr"]   """
        val comment = TemplateCommentParser.parseComment(input)

        assertEquals("template", comment.keyword)
        assertEquals("""ba,  $#@+^d\*pi&/-vr""", comment.brackets[0]["foo"])
    }

    @Test
    fun `parseComment handles opening bracket in value`() {
        val input = """@@tt-template[foo="ba[r"]   """
        val comment = TemplateCommentParser.parseComment(input)

        assertEquals("template", comment.keyword)
        assertEquals("ba[r", comment.brackets[0]["foo"])
    }

    @Test
    fun `parseComment handles closing bracket in value`() {
        val input = """@@tt-template[foo="ba]r"]   """
        val comment = TemplateCommentParser.parseComment(input)

        assertEquals("template", comment.keyword)
        assertEquals("ba]r", comment.brackets[0]["foo"])
    }

    @Test
    fun `parseComment handles line breaks`() {
        val input = """
             
             @@tt-template[foo="bar"]
             
             """.trimIndent()
        val comment = TemplateCommentParser.parseComment(input)
        
        assertEquals("template", comment.keyword)
        assertEquals("bar", comment.brackets[0]["foo"])
    }
    
    @Test
    fun `parseComment handles whitespace in brackets`() {
        val input = """@@tt-template [ foo="bar" ][ fox="bar2" ]"""
        val comment = TemplateCommentParser.parseComment(input)
        
        assertEquals("template", comment.keyword)
        assertEquals(2, comment.brackets.size)
        assertEquals("bar", comment.brackets[0]["foo"])
        assertEquals("bar2", comment.brackets[1]["fox"])
    }

    @Test
    fun `parseComment handles whitespace in brackets with multiple key-value pairs`() {
        val input = """@@tt-template [ foo="bar"   far="baz"  ][ fox="bar2" ]"""
        val comment = TemplateCommentParser.parseComment(input)

        assertEquals("template", comment.keyword)
        assertEquals(2, comment.brackets.size)
        assertEquals("bar", comment.brackets[0]["foo"])
        assertEquals("bar2", comment.brackets[1]["fox"])
    }

    @Test
    fun `parseComment handles whitespace between brackets`() {
        val input = """@@tt-template [foo="bar"] [fox="bar2"]"""
        val comment = TemplateCommentParser.parseComment(input)

        assertEquals("template", comment.keyword)
        assertEquals(2, comment.brackets.size)
        assertEquals("bar", comment.brackets[0]["foo"])
        assertEquals("bar2", comment.brackets[1]["fox"])
    }
    
    @Test
    fun `parseComment handles complex whitespace scenario`() {
        val input = """  
              
              
              @@tt-template  [  foo="bar"  ]  [  fox="bar2 bar2"  ]  
                
                """.trimIndent()
        val comment = TemplateCommentParser.parseComment(input)

        assertEquals("template", comment.keyword)
        assertEquals(2, comment.brackets.size)
        assertEquals("bar", comment.brackets[0]["foo"])
        assertEquals("bar2 bar2", comment.brackets[1]["fox"])
    }

    @Test
    fun `parseComment handles complex newline scenario`() {
        val input = """  
              
              
              @@tt-template  [  
                foo="bar"
                fee="gain"
                fox="trot"  
              ]  
              [  
                flex="grid" fox="fit"
                fix="fox"
              ]  
                
                """.trimIndent()
        val comment = TemplateCommentParser.parseComment(input)

        assertEquals("template", comment.keyword)
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
        val input = """@@tt-template[]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception with multiple empty brackets`() {
        val input = """@@tt-template[][foo="bar"][]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for duplicate keys`() {
        val input = """@@tt-template[foo="bar" foo="bar2"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for whitespaces between key and equal sign`() {
        val input = """@@tt-template[foo  ="bar"][fox="bar2 bar2"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }
    
    @Test
    fun `parseComment throws exception for whitespaces between equal sign and value`() {
        val input = """@@tt-template[foo=  "bar"][fox="bar2 bar2"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for whitespaces between key and value`() {
        val input = """@@tt-template[foo  =  "bar"][fox="bar2 bar2"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for key-value pairs with comma as separator`() {
        val input = """@@tt-template[foo="bar",far="baz"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for key-value pairs missing a space as separator`() {
        val input = """@@tt-template[foo="bar"far="baz"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for unquoted values`() {
        val input = """@@tt-template[foo=bar]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for value with quote character`() {
        val input = """@@tt-template[foo="ba"r"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for key-value pairs with missing quote at start`() {
        val input = """@@tt-template[foo=bar"][fox="bar2 bar2"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for key-value pairs with missing quote at the end`() {
        val input = """@@tt-template[foo="bar][fox="bar2 bar2"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for missing closing bracket`() {
        val input = """@@tt-template[foo="bar"[fox="bar2 bar2"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for missing opening bracket`() {
        val input = """@@tt-template [foo="bar"]fox="bar2 bar2"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }

    @Test
    fun `parseComment throws exception for attribute key with special characters`() {
        val input = """@@tt-template [foo*="bar"]"""
        assertThrows<TemplateParsingException> {
            TemplateCommentParser.parseComment(input)
        }
    }
}
