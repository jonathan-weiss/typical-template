package org.codeblessing.typicaltemplate.blackboxtests.rendertemplate

import org.codeblessing.typicaltemplate.blackboxtests.AbstractBlackboxTest
import org.junit.jupiter.api.Test

/**
 * Blackbox test for `render-template`, focused on the indentation of the embedded output.
 *
 * `RenderPageRenderer` embeds `RenderItemRenderer` (which renders two `<li>` lines). The output
 * makes the indentation behaviour explicit:
 * - default: the comment stands alone on an indented line, so the default whitespace handling removes
 *   the indentation before the placeholder; the embedded content is therefore inserted starting at
 *   column 0 and its lines are not re-indented to the surrounding `<ul>` block.
 */
class RenderTemplateBlackboxTest : AbstractBlackboxTest() {

    @Test
    fun `test that render-template embeds the sub-template without re-indenting its lines`() {
        assertSameContent(
            htmlSourcePath().resolve("rendertemplate/render-template.html"),
            htmlGeneratedPath().resolve("rendertemplate/render-template.html"),
            "rendertemplate/render-template.expectation.html",
        )
    }
}
