package org.codeblessing.typicaltemplate.blackboxtests.nestingtemplaterenderer

import org.codeblessing.typicaltemplate.blackboxtests.AbstractBlackboxTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.io.path.readText

/**
 * Blackbox test that proves a file can contain multiple template renderers and that the
 * renderer-scoped commands of one renderer do not leak into a sibling renderer.
 *
 * `nesting-template-renderer.html` defines two renderers:
 * - `FancyRenderer` uses `replace-value-by-value` (`TOKEN` -> `REPLACED-IN-A`),
 *   `replace-value-by-expression` (`DAY` -> `DayOfWeek.MONDAY`), `add-import-to-renderer`
 *   (`java.time.DayOfWeek`) and `modify-provided-filepath-by-replacements` (renaming the output file).
 * - `PlainRenderer` uses none of these.
 *
 * The non-leaking is observable in three independent ways:
 * - content: `PlainRenderer` keeps `TOKEN` and `DAY` literally, while `FancyRenderer` resolves them;
 * - file path: `FancyRenderer` is written to the renamed `fancy-output.html`, while `PlainRenderer`
 *   keeps the unmodified `nesting-template-renderer.html` path (so its expectation file is found there);
 * - imports: the generated `FancyRenderer` class imports `java.time.DayOfWeek`, the generated
 *   `PlainRenderer` class does not.
 */
class NestingTemplateRendererBlackboxTest : AbstractBlackboxTest() {

    private val template = "nestingtemplaterenderer/nesting-template-renderer.html"

    @Test
    fun `test that the renderer with inner commands produces the replaced content at the renamed path`() {
        assertSameContent(
            webAppPath().resolve(template),
            webAppGeneratedPath().resolve("nestingtemplaterenderer/fancy-output.html"),
            "nestingtemplaterenderer/fancy-output.expectation.html",
        )
    }

    @Test
    fun `test that the sibling renderer is unaffected by the inner commands of the other renderer`() {
        assertSameContent(
            webAppPath().resolve(template),
            webAppGeneratedPath().resolve("nestingtemplaterenderer/nesting-template-renderer.html"),
            "nestingtemplaterenderer/plain-output.expectation.html",
        )
    }

    @Test
    fun `test that add-import-to-renderer only adds the import to its own renderer class`() {
        val fancyRendererSource = generatedRendererSource("FancyRenderer")
        val plainRendererSource = generatedRendererSource("PlainRenderer")

        assertTrue(
            fancyRendererSource.contains("import java.time.DayOfWeek"),
            "FancyRenderer should import java.time.DayOfWeek (added via add-import-to-renderer)",
        )
        assertFalse(
            plainRendererSource.contains("DayOfWeek"),
            "PlainRenderer must not contain the DayOfWeek import added by the sibling FancyRenderer",
        )
    }

    /**
     * Reads the source of a generated template renderer class. The renderers are generated into the
     * sibling `template-renderer-executor` module, next to the example-business-project this test runs
     * against.
     */
    private fun generatedRendererSource(rendererClassName: String): String {
        val blackboxTestsRoot = kotlinSourcePath().parent.parent.parent.parent
        return blackboxTestsRoot
            .resolve("template-renderer-executor/src/typicaltemplate-generated/kotlin")
            .resolve("org/codeblessing/typicaltemplate/example/renderer")
            .resolve("$rendererClassName.kt")
            .readText()
    }
}
