package org.codeblessing.typicaltemplate.contentparsing.tokenizer

import org.codeblessing.typicaltemplate.filemapping.CommentStyles
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FileContentTokenizerTest {
    @Nested
    inner class ContentWithoutCommands {

        @Test
        fun `tokenize returns whole string as single token when no comment tokens`() {
            val input = "abc def xyz\naddn"
            val expected = listOf(PlainContentToken(input))
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize returns whole string as single token when no typical template command comments`() {
            val input = "abc /* and \n\tthis */ def xyz\naddn // until the end"
            val expected = listOf(PlainContentToken(input))
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles different line endings correctly`() {
            val input = "here we have a newline \n and than a carriage return and a newline \r\n and then a carriage return \r and the end."
            val expected = listOf(
                PlainContentToken("here we have a newline \n and than a carriage return and a newline \r\n and then a carriage return \r and the end."),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }
    }

    @Nested
    inner class KotlinBlockComments {

        @Test
        fun `tokenize handles one overall kotlin block comment`() {
            val input = "/*@@tt{{comment-content}}tt@@*/"
            val expected = listOf(
                TemplateCommentToken("comment-content"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles one overall kotlin block comment and whitespaces inside the comment`() {
            val input = "/*    \n \t   @@tt{{comment-content}}tt@@   \n\t \n   */"
            val expected = listOf(
                TemplateCommentToken("comment-content"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles one kotlin block comment surrounded by content `() {
            val input = "abc def /*  @@tt{{comment-content}}tt@@  */ uvw xyz"
            val expected = listOf(
                PlainContentToken("abc def "),
                TemplateCommentToken("comment-content"),
                PlainContentToken(" uvw xyz"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles regular kotlin comments and kotlin typical template command comments`() {
            val input = "here we have a /* @@tt{{comment-content}}tt@@ */ and than a /* kotlin comment */ and than a /*@@tt{{second-comment-content}}tt@@ */"
            val expected = listOf(
                PlainContentToken("here we have a "),
                TemplateCommentToken("comment-content"),
                PlainContentToken(" and than a /* kotlin comment */ and than a "),
                TemplateCommentToken("second-comment-content"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }


        @Test
        fun `tokenize handles adjacent comment tokens`() {
            val input = "/* @@tt{{comment-content}}tt@@*//*b*//*@@tt{{second-content}}tt@@*/"
            val expected = listOf(
                TemplateCommentToken("comment-content"),
                PlainContentToken("/*b*/"),
                TemplateCommentToken("second-content"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles comment with special characters and prefix`() {
            val input = "start /*@@tt{{!@# @tt{} 123}}tt@@*/ end /*no-prefix*/"
            val expected = listOf(
                PlainContentToken("start "),
                TemplateCommentToken("!@# @tt{} 123"),
                PlainContentToken(" end /*no-prefix*/"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles unterminated regular comment`() {
            val input = "abc /*not closed"
            val expected = listOf(PlainContentToken("abc /*not closed"))
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles unterminated typical template comment as regular content`() {
            val input = "abc /* @@tt{{-not-closed not closed"
            val expected = listOf(PlainContentToken("abc /* @@tt{{-not-closed not closed"))
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles comment at end of line`() {
            val input = "start middle /* @@tt{{end}}tt@@*/"
            val expected = listOf(
                PlainContentToken("start middle "),
                TemplateCommentToken("end"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize extracts multiple comment tokens and non-comment substrings`() {
            val input = "abc /* @@tt{{token1}}tt@@  */ def /*notemplate*/ xyz /* @@tt{{token3}}tt@@  */"
            val expected = listOf(
                PlainContentToken("abc "),
                TemplateCommentToken("token1"),
                PlainContentToken(" def /*notemplate*/ xyz "),
                TemplateCommentToken("token3"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize correctly handles multiple block comments on the same line`() {
            val input = """
                |
                |    val productCode: String/* @@tt{{if-condition[conditionExpression="field.isNullable"]}}tt@@ *//* @@tt{{print-text[text="?"]}}tt@@ *//* @@tt{{end-if-condition}}tt@@ */,
                |    
            """.trimMargin()

            val expected = listOf(
                PlainContentToken(value="\n    val productCode: String"),
                TemplateCommentToken(value="if-condition[conditionExpression=\"field.isNullable\"]"),
                TemplateCommentToken(value="print-text[text=\"?\"]"),
                TemplateCommentToken(value="end-if-condition"),
                PlainContentToken(value=",\n    "),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }
    }

    @Nested
    inner class KotlinLineComments {

        @Test
        fun `tokenize handles one overall line block comment`() {
            val input = "//@@tt{{a}}tt@@\n"
            val expected = listOf(
                TemplateCommentToken("a"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles one overall line block comment and whitespaces inside the comment`() {
            val input = "// \t \t \t @@tt{{a}}tt@@ \t "
            val expected = listOf(
                TemplateCommentToken("a"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles one kotlin line comment surrounded by content `() {
            val input = "abc def //  @@tt{{a}}tt@@  \n uvw xyz"
            val expected = listOf(
                PlainContentToken("abc def "),
                TemplateCommentToken("a"),
                PlainContentToken("\n uvw xyz"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles regular kotlin line comments and kotlin typical template command line comments`() {
            val input = "here we have a // @@tt{{command-comment}}tt@@ \n and than a // kotlin comment \n"
            val expected = listOf(
                PlainContentToken("here we have a "),
                TemplateCommentToken("command-comment"),
                PlainContentToken("\n and than a // kotlin comment \n"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles adjacent line comments`() {
            val input = "// @@tt{{a}}tt@@\n//@@tt{{b}}tt@@\n//c\n// @@tt{{d}}tt@@\n"
            val expected = listOf(
                TemplateCommentToken("a"),
                PlainContentToken("\n"),
                TemplateCommentToken("b"),
                PlainContentToken("\n//c\n"),
                TemplateCommentToken("d"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles line comment with special characters and prefix`() {
            val input = "start // @@tt{{!@# @tt{} 123}}tt@@\n end //no-prefix//"
            val expected = listOf(
                PlainContentToken("start "),
                TemplateCommentToken("!@# @tt{} 123"),
                PlainContentToken("\n end //no-prefix//"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles unterminated line comments`() {
            val input = "abc //not closed"
            val expected = listOf(PlainContentToken("abc //not closed"))
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles unterminated typical template line comment`() {
            val input = "abc // @@tt{{not-closed-by-line-end-but-by-content-end}}tt@@"
            val expected = listOf(
                PlainContentToken("abc "),
                TemplateCommentToken("not-closed-by-line-end-but-by-content-end"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles multiple line comments with and without inner content`() {
            val input = "a //@@tt{{one}}tt@@\nb //two\nc //\t\t\t @@tt{{three}}tt@@    \t"
            val expected = listOf(
                PlainContentToken("a "),
                TemplateCommentToken("one"),
                PlainContentToken("\nb //two\nc "),
                TemplateCommentToken("three"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        @Disabled
        fun `tokenize handles kotlin line comments and kotlin typical template command line comments with different line endings`() {
            val input = "here we have a // @@tt{{newline}}tt@@ \n and than a // kotlin newline comment \n and we have a // @@tt{{cr-newline}}tt@@ \r\n and than a // kotlin cr-newline comment \r\n and we have a // @@tt{{cr}}tt@@ \r and than a // kotlin cr comment \r and the end."
            val expected = listOf(
                PlainContentToken("here we have a "),
                TemplateCommentToken("newline"),
                PlainContentToken("\n and than a // kotlin newline comment \n and we have a "),
                TemplateCommentToken("cr-newline"),
                PlainContentToken("\r\n and than a // kotlin cr-newline comment \r\n and we have a "),
                TemplateCommentToken("cr"),
                PlainContentToken("\r and than a // kotlin cr comment \r and the end."),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }
    }

    @Nested
    inner class KotlinLineAndBlockToken {

        @Test
        fun `tokenize handles mixed block and line comments`() {
            val input = "a /* @@tt{{block}}tt@@*/ b // @@tt{{line}}tt@@\nc /*no-prefix*/ //notemplate"
            val expected = listOf(
                PlainContentToken("a "),
                TemplateCommentToken("block"),
                PlainContentToken(" b "),
                TemplateCommentToken("line"),
                PlainContentToken("\nc /*no-prefix*/ //notemplate"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

        @Test
        fun `tokenize handles block and line comments on same line`() {
            val input = "a /* @@tt{{block}}tt@@*/ //line\nb /*no-prefix*/ // @@tt{{line}}tt@@"
            val expected = listOf(
                PlainContentToken("a "),
                TemplateCommentToken("block"),
                PlainContentToken(" //line\nb /*no-prefix*/ "),
                TemplateCommentToken("line"),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }
    }

    @Nested
    inner class KotlinMultipleCommandsPerComment {

        @Test
        fun `tokenize correctly with two delimited comments that strip each the begin and end of line`() {
            val input = """
                |/* 
                |@@tt{{
                |template-renderer [
                |    templateRendererClassName="EntityDtoTemplateRenderer"
                |    templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
                |]
                |
                |
                |template-model [
                |    modelClassName="DtoEntityRenderModel"
                |    modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
                |    modelName="model"
                |]
                |}}tt@@
                |*/
            """.trimMargin()
            val expected = listOf(
                TemplateCommentToken(
                    "\n" +
                            "template-renderer [\n" +
                            "    templateRendererClassName=\"EntityDtoTemplateRenderer\"\n" +
                            "    templateRendererPackageName=\"org.codeblessing.typicaltemplate.example.renderer\"\n" +
                            "]\n" +
                            "\n" +
                            "\n" +
                            "template-model [\n" +
                            "    modelClassName=\"DtoEntityRenderModel\"\n" +
                            "    modelPackageName=\"org.codeblessing.typicaltemplate.example.renderer.model\"\n" +
                            "    modelName=\"model\"\n" +
                            "]\n"
                ),
            )
            Assertions.assertEquals(
                expected,
                FileContentTokenizer.tokenizeContent(input, CommentStyles.KOTLIN_COMMENT_STYLES)
            )
        }

    }
}
