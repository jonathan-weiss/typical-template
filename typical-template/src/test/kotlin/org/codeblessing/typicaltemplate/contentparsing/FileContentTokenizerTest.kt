package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.contentparsing.FileContentTokenizer.PlainContentToken
import org.codeblessing.typicaltemplate.contentparsing.FileContentTokenizer.TemplateCommentToken
import org.codeblessing.typicaltemplate.filemapping.ContentMapper.KOTLIN_COMMENT_STYLES
import org.junit.jupiter.api.Assertions.assertEquals
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
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize returns whole string as single token when no typical template command comments`() {
            val input = "abc /* and \n\tthis */ def xyz\naddn // until the end"
            val expected = listOf(PlainContentToken(input))
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }
    }

    @Nested
    inner class KotlinBlockComments {

        @Test
        fun `tokenize handles one overall kotlin block comment`() {
            val input = "/*@@tt-a*/"
            val expected = listOf(
                TemplateCommentToken("@@tt-a"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles one overall kotlin block comment and whitespaces inside the comment`() {
            val input = "/* \n @@tt-a \n\t \n */"
            val expected = listOf(
                TemplateCommentToken("@@tt-a"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles one kotlin block comment surrounded by content `() {
            val input = "abc def /*  @@tt-a  */ uvw xyz"
            val expected = listOf(
                PlainContentToken("abc def "),
                TemplateCommentToken("@@tt-a"),
                PlainContentToken(" uvw xyz"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles regular kotlin comments and kotlin typical template command comments`() {
            val input = "here we have a /* @@tt-command-comment*/ and than a /* kotlin comment */"
            val expected = listOf(
                PlainContentToken("here we have a "),
                TemplateCommentToken("@@tt-command-comment"),
                PlainContentToken(" and than a /* kotlin comment */"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }


        @Test
        fun `tokenize handles adjacent comment tokens`() {
            val input = "/* @@tt-a*//*b*//* @@tt-c*/"
            val expected = listOf(
                TemplateCommentToken("@@tt-a"),
                PlainContentToken("/*b*/"),
                TemplateCommentToken("@@tt-c"),
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
        fun `tokenize handles unterminated regular comment`() {
            val input = "abc /*not closed"
            val expected = listOf(PlainContentToken("abc /*not closed"))
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles unterminated typical template comment as regular content`() {
            val input = "abc /* @@tt-not-closed not closed"
            val expected = listOf(PlainContentToken("abc /* @@tt-not-closed not closed"))
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles comment at end of line`() {
            val input = "start middle /* @@tt-end*/"
            val expected = listOf(
                PlainContentToken("start middle "),
                TemplateCommentToken("@@tt-end"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize extracts multiple comment tokens and non-comment substrings`() {
            val input = "abc /* @@tt-token1  */ def /*notemplate*/ xyz /* @@tt-token3  */"
            val expected = listOf(
                PlainContentToken("abc "),
                TemplateCommentToken("@@tt-token1"),
                PlainContentToken(" def /*notemplate*/ xyz "),
                TemplateCommentToken("@@tt-token3"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }
    }

    @Nested
    inner class KotlinLineComments {

        @Test
        fun `tokenize handles one overall line block comment`() {
            val input = "//@@tt-a\n"
            val expected = listOf(
                TemplateCommentToken("@@tt-a"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles one overall line block comment and whitespaces inside the comment`() {
            val input = "// \t \t \t @@tt-a \t "
            val expected = listOf(
                TemplateCommentToken("@@tt-a"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles one kotlin line comment surrounded by content `() {
            val input = "abc def //  @@tt-a  \n uvw xyz"
            val expected = listOf(
                PlainContentToken("abc def "),
                TemplateCommentToken("@@tt-a"),
                PlainContentToken("\n uvw xyz"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles regular kotlin line comments and kotlin typical template command line comments`() {
            val input = "here we have a // @@tt-command-comment \n and than a // kotlin comment \n"
            val expected = listOf(
                PlainContentToken("here we have a "),
                TemplateCommentToken("@@tt-command-comment"),
                PlainContentToken("\n and than a // kotlin comment \n"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles adjacent line comments`() {
            val input = "// @@tt-a\n//@@tt-b\n//c\n// @@tt-d\n"
            val expected = listOf(
                TemplateCommentToken("@@tt-a"),
                PlainContentToken("\n"),
                TemplateCommentToken("@@tt-b"),
                PlainContentToken("\n//c\n"),
                TemplateCommentToken("@@tt-d"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles line comment with special characters and prefix`() {
            val input = "start // @@tt-!@# 123\n end //no-prefix//"
            val expected = listOf(
                PlainContentToken("start "),
                TemplateCommentToken("@@tt-!@# 123"),
                PlainContentToken("\n end //no-prefix//"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles unterminated line comments`() {
            val input = "abc //not closed"
            val expected = listOf(PlainContentToken("abc //not closed"))
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles unterminated typical template line comment`() {
            val input = "abc // @@tt-not-closed-by-line-end"
            val expected = listOf(
                PlainContentToken("abc "),
                TemplateCommentToken("@@tt-not-closed-by-line-end"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles multiple line comments with and without inner content`() {
            val input = "a //@@tt-one\nb //two\nc //\t\t\t @@tt-three    \t"
            val expected = listOf(
                PlainContentToken("a "),
                TemplateCommentToken("@@tt-one"),
                PlainContentToken("\nb //two\nc "),
                TemplateCommentToken("@@tt-three"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        @Disabled
        fun `tokenize handles kotlin line comments and kotlin typical template command line comments with different line endings`() {
            val input = "here we have a // @@tt-newline \n and than a // kotlin newline comment \n and we have a // @@tt-cr-newline \r\n and than a // kotlin cr-newline comment \r\n and we have a // @@tt-cr \r and than a // kotlin cr comment \r and the end."
            val expected = listOf(
                PlainContentToken("here we have a "),
                TemplateCommentToken("@@tt-newline"),
                PlainContentToken("\n and than a // kotlin newline comment \n and we have a "),
                TemplateCommentToken("@@tt-cr-newline"),
                PlainContentToken("\r\n and than a // kotlin cr-newline comment \r\n and we have a "),
                TemplateCommentToken("@@tt-cr"),
                PlainContentToken("\r and than a // kotlin cr comment \r and the end."),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }
    }

    @Nested
    inner class KotlinLineAndBlockToken {

        @Test
        fun `tokenize handles mixed block and line comments`() {
            val input = "a /* @@tt-block*/ b // @@tt-line\nc /*no-prefix*/ //notemplate"
            val expected = listOf(
                PlainContentToken("a "),
                TemplateCommentToken("@@tt-block"),
                PlainContentToken(" b "),
                TemplateCommentToken("@@tt-line"),
                PlainContentToken("\nc /*no-prefix*/ //notemplate"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize handles block and line comments on same line`() {
            val input = "a /* @@tt-block*/ //line\nb /*no-prefix*/ // @@tt-line"
            val expected = listOf(
                PlainContentToken("a "),
                TemplateCommentToken("@@tt-block"),
                PlainContentToken(" //line\nb /*no-prefix*/ "),
                TemplateCommentToken("@@tt-line"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }
    }

    @Nested
    inner class KotlinLineAndBlockStripBeginAndEndOfLine {

        @Test
        fun `tokenize removes begin of line if marker ignore-line-before is set`() {
            val input = "start here\na stripped begin of line /* @@<# @@tt-block */ but not at the end of line\n and a stripped begin of line // @@<# @@tt-line\n but a new line at the end of line."
            val expected = listOf(
                PlainContentToken("start here"),
                TemplateCommentToken("@@tt-block"),
                PlainContentToken(" but not at the end of line"),
                TemplateCommentToken("@@tt-line"),
                PlainContentToken("\n but a new line at the end of line."),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize removes end of line if marker ignore-line-after is set`() {
            val input = "start here\nand keep begin of line /* @@tt-block @@># */ but do not keep the end of line\n and again keep begin of line // @@tt-line @@># \n but remove the line-break."
            val expected = listOf(
                PlainContentToken("start here\nand keep begin of line "),
                TemplateCommentToken("@@tt-block"),
                PlainContentToken(" and again keep begin of line "),
                TemplateCommentToken("@@tt-line"),
                PlainContentToken(" but remove the line-break."),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize removes begin and end of line if marker ignore-line-before and ignore-line-after is set`() {
            val input = "start here\na stripped begin of line /* @@<# @@tt-block  @@>#  */ and also at the end of line\nbut no line in between\n and a stripped begin of line // @@<# @@tt-line @@>#  \nbut not a comment free line."
            val expected = listOf(
                PlainContentToken("start here"),
                TemplateCommentToken("@@tt-block"),
                PlainContentToken("but no line in between"),
                TemplateCommentToken("@@tt-line"),
                PlainContentToken("but not a comment free line."),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        fun `tokenize correctly with two delimited comments that strip each the begin and end of line`() {
            val input = """
                |/* @@<#
                |@@tt-template-renderer [
                |    templateRendererClassName="EntityDtoTemplateRenderer"
                |    templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
                |] @@>#
                |*/
                |/* @@<#
                |@@tt-template-model [
                |    modelClassName="DtoEntityRenderModel"
                |    modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
                |    modelName="model"
                |]
                |*/
            """.trimMargin()
            val expected = listOf(
                TemplateCommentToken("@@tt-template-renderer [\n" +
                        "    templateRendererClassName=\"EntityDtoTemplateRenderer\"\n" +
                        "    templateRendererPackageName=\"org.codeblessing.typicaltemplate.example.renderer\"\n" +
                        "]"),
                TemplateCommentToken("@@tt-template-model [\n" +
                        "    modelClassName=\"DtoEntityRenderModel\"\n" +
                        "    modelPackageName=\"org.codeblessing.typicaltemplate.example.renderer.model\"\n" +
                        "    modelName=\"model\"\n" +
                        "]"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

        @Test
        @Disabled
        fun `tokenize correctly handles multiple block comments on the same line`() {
            val input = """
                |
                |    val productCode: String/* @@tt-if-condition[conditionExpression="field.isNullable"] *//* @@tt-print-text[text="?"] *//* @@tt-end-if-condition */,
                |    
            """.trimMargin()
            val expected = listOf(
                TemplateCommentToken("@@tt-template-renderer [\n" +
                        "    templateRendererClassName=\"EntityDtoTemplateRenderer\"\n" +
                        "    templateRendererPackageName=\"org.codeblessing.typicaltemplate.example.renderer\"\n" +
                        "]"),
                TemplateCommentToken("@@tt-template-model [\n" +
                        "    modelClassName=\"DtoEntityRenderModel\"\n" +
                        "    modelPackageName=\"org.codeblessing.typicaltemplate.example.renderer.model\"\n" +
                        "    modelName=\"model\"\n" +
                        "]"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

    }


    @Nested
    inner class KotlinMultipleCommandsPerComment {

        @Test
        @Disabled
        fun `tokenize correctly with two delimited comments that strip each the begin and end of line`() {
            val input = """
                |/* 
                |@@tt-template-renderer [
                |    templateRendererClassName="EntityDtoTemplateRenderer"
                |    templateRendererPackageName="org.codeblessing.typicaltemplate.example.renderer"
                |] 
                |
                |
                |@@tt-template-model [
                |    modelClassName="DtoEntityRenderModel"
                |    modelPackageName="org.codeblessing.typicaltemplate.example.renderer.model"
                |    modelName="model"
                |]
                |*/
            """.trimMargin()
            val expected = listOf(
                TemplateCommentToken("@@tt-template-renderer [\n" +
                        "    templateRendererClassName=\"EntityDtoTemplateRenderer\"\n" +
                        "    templateRendererPackageName=\"org.codeblessing.typicaltemplate.example.renderer\"\n" +
                        "]"),
                TemplateCommentToken("@@tt-template-model [\n" +
                        "    modelClassName=\"DtoEntityRenderModel\"\n" +
                        "    modelPackageName=\"org.codeblessing.typicaltemplate.example.renderer.model\"\n" +
                        "    modelName=\"model\"\n" +
                        "]"),
            )
            assertEquals(expected, FileContentTokenizer.tokenizeContent(input, KOTLIN_COMMENT_STYLES))
        }

    }

}
