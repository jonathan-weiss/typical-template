package org.codeblessing.typicaltemplate.application

import org.codeblessing.typicaltemplate.ClasspathResourceLoader
import org.codeblessing.typicaltemplate.ClasspathResourceWriter
import org.codeblessing.typicaltemplate.RelativeFile
import org.codeblessing.typicaltemplate.filemapping.CommentStyles.HTML_COMMENT_STYLES
import org.junit.jupiter.api.Assertions.assertEquals
import java.nio.file.Paths

private const val OVERWRITE_EXPECTED_TEXT = false // do only active during developement

abstract class AbstractCommandTest {

    fun assertExpectedGeneratedText(contentToParse: String, expectedClasspathResourceName: String) {
        val result = ContentToTemplateRendererTransformer.parseContentAndCreateTemplateRenderers(
            filepath = RelativeFile.fromRelativeString("input/my-renderer.html"),
            contentToParse = contentToParse,
            supportedCommentStyles = HTML_COMMENT_STYLES,
            targetBasePath = Paths.get("/output"),
        )
        val actualContent = result.single().templateRendererClassContent

        val classpathResourcePath = "org/codeblessing/typicaltemplate/application/$expectedClasspathResourceName"
        val expectedContent = if(OVERWRITE_EXPECTED_TEXT) {
            ClasspathResourceWriter.writeClasspathResource(classpathResourcePath, actualContent)
            actualContent
        } else {
            ClasspathResourceLoader.loadClasspathResource(
                classpathResourcePath = "org/codeblessing/typicaltemplate/application/$expectedClasspathResourceName",
            )
        }

        assertEquals(expectedContent, result.single().templateRendererClassContent)
    }
}
