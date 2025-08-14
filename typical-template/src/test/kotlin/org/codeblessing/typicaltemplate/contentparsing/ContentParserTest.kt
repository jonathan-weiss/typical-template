package org.codeblessing.typicaltemplate.contentparsing

import org.codeblessing.typicaltemplate.filemapping.CommentStyles.HTML_COMMENT_STYLES
import org.codeblessing.typicaltemplate.filemapping.CommentStyles.SCSS_COMMENT_STYLES
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals

class ContentParserTest {
    @Test
    fun `parse the template of a example html file should render the template`() {
        val resourcePath = "/org/codeblessing/typicaltemplate/contentparsing/my-address-form.html"
        val resource = requireNotNull(this.javaClass.getResourceAsStream(resourcePath)) {
            "Resource $resourcePath not found"
        }

        val htmlContent = resource.readBytes().toString(StandardCharsets.UTF_8)

        val templates = ContentParser.parseContent(htmlContent, HTML_COMMENT_STYLES)
        assertEquals(1, templates.size)
    }

    @Test
    fun `parse the template of a empty file should not fail`() {
        val emptyContent = ""

        val templates = ContentParser.parseContent(emptyContent, SCSS_COMMENT_STYLES)
        assertEquals(0, templates.size)
    }

    @Test
    fun `parse the template of a file with empty comment styles should not fail`() {
        val myContent = "foo bar"

        val templates = ContentParser.parseContent(myContent, emptyList())
        assertEquals(0, templates.size)
    }

}
