package org.codeblessing.typicaltemplate.contentparsing.tokenizer

import org.codeblessing.typicaltemplate.CommentStyles
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
            val expected = listOf(PlainTextContentPart(input))
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize returns whole string as single token when no typical template command comments`() {
            val input = "abc /* and \n\tthis */ def xyz\naddn // until the end"
            val expected = listOf(PlainTextContentPart(input))
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles different line endings correctly`() {
            val input = "here we have a newline \n and than a carriage return and a newline \r\n and then a carriage return \r and the end."
            val expected = listOf(
                PlainTextContentPart("here we have a newline \n and than a carriage return and a newline \r\n and then a carriage return \r and the end."),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }
    }

    @Nested
    inner class KotlinBlockComments {

        @Test
        fun `tokenize handles one overall kotlin block comment`() {
            val input = "/*@tt{{{comment-content}}}@*/"
            val expected = listOf(
                TemplateCommentContentPart("comment-content"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles one overall kotlin block comment and whitespaces inside the comment`() {
            val input = "/*    \n \t   @tt{{{comment-content}}}@   \n\t \n   */"
            val expected = listOf(
                TemplateCommentContentPart("comment-content"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles one kotlin block comment surrounded by content `() {
            val input = "abc def /*  @tt{{{comment-content}}}@  */ uvw xyz"
            val expected = listOf(
                PlainTextContentPart("abc def "),
                TemplateCommentContentPart("comment-content"),
                PlainTextContentPart(" uvw xyz"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles regular kotlin comments and kotlin typical template command comments`() {
            val input = "here we have a /* @tt{{{comment-content}}}@ */ and than a /* kotlin comment */ and than a /*@tt{{{second-comment-content}}}@ */"
            val expected = listOf(
                PlainTextContentPart("here we have a "),
                TemplateCommentContentPart("comment-content"),
                PlainTextContentPart(" and than a /* kotlin comment */ and than a "),
                TemplateCommentContentPart("second-comment-content"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }


        @Test
        fun `tokenize handles adjacent comment tokens`() {
            val input = "/* @tt{{{comment-content}}}@*//*b*//*@tt{{{second-content}}}@*/"
            val expected = listOf(
                TemplateCommentContentPart("comment-content"),
                PlainTextContentPart("/*b*/"),
                TemplateCommentContentPart("second-content"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles comment with special characters and prefix`() {
            val input = "start /*@tt{{{!@# @tt{} 123}}}@*/ end /*no-prefix*/"
            val expected = listOf(
                PlainTextContentPart("start "),
                TemplateCommentContentPart("!@# @tt{} 123"),
                PlainTextContentPart(" end /*no-prefix*/"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles unterminated regular comment`() {
            val input = "abc /*not closed"
            val expected = listOf(PlainTextContentPart("abc /*not closed"))
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles unterminated typical template comment as regular content`() {
            val input = "abc /* @tt{{{-not-closed not closed"
            val expected = listOf(PlainTextContentPart("abc /* @tt{{{-not-closed not closed"))
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles comment at end of line`() {
            val input = "start middle /* @tt{{{end}}}@*/"
            val expected = listOf(
                PlainTextContentPart("start middle "),
                TemplateCommentContentPart("end"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize extracts multiple comment tokens and non-comment substrings`() {
            val input = "abc /* @tt{{{token1}}}@  */ def /*notemplate*/ xyz /* @tt{{{token3}}}@  */"
            val expected = listOf(
                PlainTextContentPart("abc "),
                TemplateCommentContentPart("token1"),
                PlainTextContentPart(" def /*notemplate*/ xyz "),
                TemplateCommentContentPart("token3"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize correctly handles multiple block comments on the same line`() {
            val input = """
                |
                |    val productCode: String/* @tt{{{if[conditionExpression="field.isNullable"]}}}@ *//* @tt{{{print-text[text="?"]}}}@ *//* @tt{{{end-if}}}@ */,
                |    
            """.trimMargin()

            val expected = listOf(
                PlainTextContentPart(value="\n    val productCode: String"),
                TemplateCommentContentPart(value="if[conditionExpression=\"field.isNullable\"]"),
                TemplateCommentContentPart(value="print-text[text=\"?\"]"),
                TemplateCommentContentPart(value="end-if"),
                PlainTextContentPart(value=",\n    "),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }
    }

    @Nested
    inner class KotlinLineComments {

        @Test
        fun `tokenize handles one overall line block comment`() {
            val input = "//@tt{{{a}}}@\n"
            val expected = listOf(
                TemplateCommentContentPart("a"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles one overall line block comment and whitespaces inside the comment`() {
            val input = "// \t \t \t @tt{{{a}}}@ \t "
            val expected = listOf(
                TemplateCommentContentPart("a"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles one kotlin line comment surrounded by content `() {
            val input = "abc def //  @tt{{{a}}}@  \n uvw xyz"
            val expected = listOf(
                PlainTextContentPart("abc def "),
                TemplateCommentContentPart("a"),
                PlainTextContentPart("\n uvw xyz"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles regular kotlin line comments and kotlin typical template command line comments`() {
            val input = "here we have a // @tt{{{command-comment}}}@ \n and than a // kotlin comment \n"
            val expected = listOf(
                PlainTextContentPart("here we have a "),
                TemplateCommentContentPart("command-comment"),
                PlainTextContentPart("\n and than a // kotlin comment \n"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles adjacent line comments`() {
            val input = "// @tt{{{a}}}@\n//@tt{{{b}}}@\n//c\n// @tt{{{d}}}@\n"
            val expected = listOf(
                TemplateCommentContentPart("a"),
                PlainTextContentPart("\n"),
                TemplateCommentContentPart("b"),
                PlainTextContentPart("\n//c\n"),
                TemplateCommentContentPart("d"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles line comment with special characters and prefix`() {
            val input = "start // @tt{{{!@# @tt{} 123}}}@\n end //no-prefix//"
            val expected = listOf(
                PlainTextContentPart("start "),
                TemplateCommentContentPart("!@# @tt{} 123"),
                PlainTextContentPart("\n end //no-prefix//"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles unterminated line comments`() {
            val input = "abc //not closed"
            val expected = listOf(PlainTextContentPart("abc //not closed"))
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles unterminated typical template line comment`() {
            val input = "abc // @tt{{{not-closed-by-line-end-but-by-content-end}}}@"
            val expected = listOf(
                PlainTextContentPart("abc "),
                TemplateCommentContentPart("not-closed-by-line-end-but-by-content-end"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles multiple line comments with and without inner content`() {
            val input = "a //@tt{{{one}}}@\nb //two\nc //\t\t\t @tt{{{three}}}@    \t"
            val expected = listOf(
                PlainTextContentPart("a "),
                TemplateCommentContentPart("one"),
                PlainTextContentPart("\nb //two\nc "),
                TemplateCommentContentPart("three"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        @Disabled
        fun `tokenize handles kotlin line comments and kotlin typical template command line comments with different line endings`() {
            val input = "here we have a // @tt{{{newline}}}@ \n and than a // kotlin newline comment \n and we have a // @tt{{{cr-newline}}}@ \r\n and than a // kotlin cr-newline comment \r\n and we have a // @tt{{{cr}}}@ \r and than a // kotlin cr comment \r and the end."
            val expected = listOf(
                PlainTextContentPart("here we have a "),
                TemplateCommentContentPart("newline"),
                PlainTextContentPart("\n and than a // kotlin newline comment \n and we have a "),
                TemplateCommentContentPart("cr-newline"),
                PlainTextContentPart("\r\n and than a // kotlin cr-newline comment \r\n and we have a "),
                TemplateCommentContentPart("cr"),
                PlainTextContentPart("\r and than a // kotlin cr comment \r and the end."),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }
    }

    @Nested
    inner class KotlinLineAndBlockContentPart {

        @Test
        fun `tokenize handles mixed block and line comments`() {
            val input = "a /* @tt{{{block}}}@*/ b // @tt{{{line}}}@\nc /*no-prefix*/ //notemplate"
            val expected = listOf(
                PlainTextContentPart("a "),
                TemplateCommentContentPart("block"),
                PlainTextContentPart(" b "),
                TemplateCommentContentPart("line"),
                PlainTextContentPart("\nc /*no-prefix*/ //notemplate"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }

        @Test
        fun `tokenize handles block and line comments on same line`() {
            val input = "a /* @tt{{{block}}}@*/ //line\nb /*no-prefix*/ // @tt{{{line}}}@"
            val expected = listOf(
                PlainTextContentPart("a "),
                TemplateCommentContentPart("block"),
                PlainTextContentPart(" //line\nb /*no-prefix*/ "),
                TemplateCommentContentPart("line"),
            )
            Assertions.assertEquals(
                expected,
                 tokenizeContent(input)
            )
        }
    }

    @Nested
    inner class KotlinMultipleCommandsPerComment {

        @Test
        fun `tokenize correctly with two delimited comments that strip each the begin and end of line`() {
            val input = """
                |/* 
                |@tt{{{
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
                |}}}@
                |*/
            """.trimMargin()
            val expected = listOf(
                TemplateCommentContentPart(
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
                 tokenizeContent(input)
            )
        }

    }
    
    private fun tokenizeContent(content: String): List<ContentPart> {
        return FileContentTokenizer.tokenizeContent(content, CommentStyles.KOTLIN_COMMENT_STYLES).map { it.contentPart }
    }
}
