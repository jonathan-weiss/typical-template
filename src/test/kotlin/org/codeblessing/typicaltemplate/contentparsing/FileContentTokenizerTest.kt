package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.contentparsing.FileContentTokenizer.PlainContentToken
import org.codeblessing.typicaltemplate.contentparsing.FileContentTokenizer.TemplateCommentToken
import org.codeblessing.typicaltemplate.filemapping.ContentMapper.KOTLIN_COMMENT_STYLES
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class FileContentTokenizerTest {
    @Test
    fun `tokenize extracts multiple comment tokens and non-comment substrings`() {
        val input = "abc /* @@tt-token1  */ def /*notemplate*/ xyz /* @@tt-token3  */"
        val expected = listOf(
            PlainContentToken("abc "),
            TemplateCommentToken("@@tt-token1"),
            PlainContentToken(" def /*notemplate*/ xyz "),
            TemplateCommentToken("@@tt-token3")
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize returns whole string as single token when no comment tokens`() {
        val input = "abc def xyz"
        val expected = listOf(PlainContentToken("abc def xyz"))
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles adjacent comment tokens with and without prefix`() {
        val input = "/* @@tt-a*//*b*//* @@tt-c*/"
        val expected = listOf(
            TemplateCommentToken("@@tt-a"),
            PlainContentToken("/*b*/"),
            TemplateCommentToken("@@tt-c")
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles comment with special characters and prefix`() {
        val input = "start /* @@tt-!@# 123*/ end /*no-prefix*/"
        val expected = listOf(
            PlainContentToken("start "),
            TemplateCommentToken("@@tt-!@# 123"),
            PlainContentToken(" end /*no-prefix*/"),
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles unterminated comment`() {
        val input = "abc /*not closed"
        val expected = listOf(PlainContentToken("abc /*not closed"))
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles comment at start with prefix`() {
        val input = "/* @@tt-start*/ middle /*end*/"
        val expected = listOf(
            TemplateCommentToken("@@tt-start"),
            PlainContentToken(" middle /*end*/"),
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles comment at end with prefix`() {
        val input = "start middle /* @@tt-end*/"
        val expected = listOf(
            PlainContentToken("start middle "),
            TemplateCommentToken("@@tt-end"),
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    // --- New tests for line comments ---

    @Test
    fun `tokenize handles single line comment with and without prefix`() {
        val input = "abc // @@tt-this is a command\ndef // just a comment\nghi"
        val expected = listOf(
            PlainContentToken("abc "),
            TemplateCommentToken("@@tt-this is a command"),
            PlainContentToken("\ndef // just a comment\nghi"),
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles line comment at end of input with and without prefix`() {
        val input = "abc // @@tt-comment at end\ndef // not a template"
        val expected = listOf(
            PlainContentToken("abc "),
            TemplateCommentToken("@@tt-comment at end"),
            PlainContentToken("\ndef // not a template")
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles multiple line comments with and without prefix`() {
        val input = "a // @@tt-one\nb //two\nc // @@tt-three"
        val expected = listOf(
            PlainContentToken("a "),
            TemplateCommentToken(" @@tt-one"),
            PlainContentToken("\nb "),
            PlainContentToken("two"),
            PlainContentToken("\nc "),
            TemplateCommentToken(" @@tt-three")
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles block and line comments mixed with and without prefix`() {
        val input = "a /* @@tt-block*/ b // @@tt-line\nc /*no-prefix*/ //notemplate"
        val expected = listOf(
            PlainContentToken("a "),
            TemplateCommentToken("@@tt-block"),
            PlainContentToken(" b "),
            TemplateCommentToken(" @@tt-line"),
            PlainContentToken("\nc /*no-prefix*/ //notemplate"),
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles block and line comments on same line with and without prefix`() {
        val input = "a /* @@tt-block*/ //line\nb /*no-prefix*/ // @@tt-line"
        val expected = listOf(
            PlainContentToken("a "),
            TemplateCommentToken(" @@tt-block"),
            PlainContentToken(" "),
            PlainContentToken("line"),
            PlainContentToken("\nb "),
            PlainContentToken("no-prefix"),
            PlainContentToken(" "),
            TemplateCommentToken(" @@tt-line")
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles block comment with one space before COMMAND_PREFIX`() {
        val input = "abc /* @@tt-block*/ def"
        val expected = listOf(
            PlainContentToken("abc "),
            TemplateCommentToken(" @@tt-block"),
            PlainContentToken(" def")
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles line comment with one space before COMMAND_PREFIX`() {
        val input = "abc // @@tt-line\ndef"
        val expected = listOf(
            PlainContentToken("abc "),
            TemplateCommentToken(" @@tt-line"),
            PlainContentToken("\ndef")
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }

    @Test
    fun `tokenize handles block and line comments with one leading and trailing space before COMMAND_PREFIX`() {
        val input = "/* @@tt-block */ // @@tt-line \n"
        val expected = listOf(
            TemplateCommentToken(" @@tt-block "),
            PlainContentToken(" "),
            TemplateCommentToken(" @@tt-line "),
            PlainContentToken("\n")
        )
        assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
    }
}
