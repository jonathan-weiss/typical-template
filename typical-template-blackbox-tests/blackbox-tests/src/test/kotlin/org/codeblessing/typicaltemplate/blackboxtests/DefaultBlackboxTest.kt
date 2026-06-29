package org.codeblessing.typicaltemplate.blackboxtests

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.nio.file.Path

class DefaultBlackboxTest : AbstractBlackboxTest() {

    enum class SourceType(val fileExtension: String) {
        KOTLIN("kt"),
        HTML("html"),
    }

    enum class TestDescription(
        val sourceType: SourceType,
        val subpackage: String,
        val templateName: String,
        val outputName: String = templateName,
    ) {
        FOREACH(
            SourceType.HTML,
            subpackage = "foreach",
            templateName = "foreach",
        ),
        MOVE(
            SourceType.HTML,
            subpackage = "move",
            templateName = "move",
        ),
        MOVE_DEFAULT(
            SourceType.HTML,
            subpackage = "move",
            templateName = "move-default",
        ),
        NESTING(
            SourceType.HTML,
            subpackage = "nesting",
            templateName = "nesting",
        ),
        NESTING_REPLACES(
            SourceType.HTML,
            subpackage = "nestingreplaces",
            templateName = "nesting-replaces",
        ),
//        TEMPLATE_RENDERER(
//            SourceType.HTML,
//            subpackage = "nestingtemplaterenderer",
//            templateName = "nesting-template-renderer",
//            outputName = "fancy-output"
//        ),
        RENDER_TEMPLATE(
            SourceType.HTML,
            subpackage = "rendertemplate",
            templateName = "render-template",
        ),
        WHITESPACE(
            SourceType.HTML,
            subpackage = "whitespace",
            templateName = "whitespace",
        ),
        ;

        val fileExtension: String = sourceType.fileExtension
    }

    @ParameterizedTest
    @EnumSource(TestDescription::class)
    fun `test output of move-comment-backward and move-comment-forward commands`(description: TestDescription) {
        assertSameContent(
            fileWithTypicalTemplateSyntax = sourceBasePath(description).resolve("${description.subpackage}/${description.templateName}.${description.fileExtension}"),
            generatedFile = generatedSourceBasePath(description).resolve("${description.subpackage}/${description.templateName}.${description.fileExtension}"),
            expectedContentResourceName = "${description.subpackage}/${description.outputName}.expectation.${description.fileExtension}",
        )
    }

    private fun sourceBasePath(description: TestDescription): Path {
        return when (description.sourceType) {
            SourceType.KOTLIN -> kotlinSourcePath()
            SourceType.HTML -> htmlSourcePath()
        }
    }

    private fun generatedSourceBasePath(description: TestDescription): Path {
        return when (description.sourceType) {
            SourceType.KOTLIN -> kotlinGeneratedPath()
            SourceType.HTML -> htmlGeneratedPath()
        }
    }

}
