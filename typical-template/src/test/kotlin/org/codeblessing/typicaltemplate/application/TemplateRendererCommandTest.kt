package org.codeblessing.typicaltemplate.application

import org.codeblessing.typicaltemplate.RelativeFile
import org.codeblessing.typicaltemplate.filemapping.CommentStyles.HTML_COMMENT_STYLES
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class TemplateRendererCommandTest {

    @Test
    fun `parse template renderer command and create kotlin renderer class`() {
        val contentToParse = """
            <!-- @tt{{{
               @template-renderer [
                templateRendererClassName="MyRenderer"
                templateRendererPackageName="com.example"
               ]
            }}}@ -->
            <div>Hello World</div>
        """.trimIndent()

        val tq = "\"\"\""
        val expectedContent = """
            /*
             * This file is generated using typical-template.
             */
            package com.example



            /**
             * Generate the content for the template MyRenderer filled up
             * with the content of the passed models.
             */
            object MyRenderer {

                fun renderTemplate(): String {
                    return $tq
                      |
                      |<div>Hello World</div>
                    $tq.trimMargin(marginPrefix = "|")
                }

                fun filePath(): String {
                  return "input/my-renderer.html"
                }
            }
        """.trimIndent()

        val result = ContentToTemplateRendererTransformer.parseContentAndCreateTemplateRenderers(
            filepath = RelativeFile.fromRelativeString("input/my-renderer.html"),
            contentToParse = contentToParse,
            supportedCommentStyles = HTML_COMMENT_STYLES,
            targetBasePath = Paths.get("/output"),
        )

        assertEquals(expectedContent, result.single().templateRendererClassContent)
    }
}
