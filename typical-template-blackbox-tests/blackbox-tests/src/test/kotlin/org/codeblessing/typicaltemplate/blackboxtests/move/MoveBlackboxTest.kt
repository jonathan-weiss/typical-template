package org.codeblessing.typicaltemplate.blackboxtests.move

import org.codeblessing.typicaltemplate.blackboxtests.AbstractBlackboxTest
import org.junit.jupiter.api.Test

/**
 * Blackbox tests for `move-comment-backward` (`@mvb`) and `move-comment-forward` (`@mvf`).
 *
 * XML is used on purpose because an XML document must start with the preamble
 * `<?xml version="1.0" encoding="UTF-8"?>`, and it is not possible to put a comment before that
 * preamble. The tests therefore show the two extremes:
 * - [move-default.xml] keeps the `@template-renderer` comment where it is written (after the
 *   preamble), so the preamble is *not* part of the template output.
 * - [move.xml] uses `@move-comment-backward` on the very same comment to move it above the preamble,
 *   so the preamble becomes part of the rendered output.
 *
 * [move.xml] additionally demonstrates the remaining behaviour described in the command reference:
 * - moving a comment into the middle of an XML tag to *insert* an attribute (`@mvb` + `@print-text`),
 * - moving a comment into the middle of an element to *ignore* a child element (`@mvf` + `@ignore-text`),
 * - that a move travels across a normal (non-typical-template) XML comment, and
 * - that a move never crosses the next typical-template comment: `(last)` is inserted at the last
 *   comma *before* the following comment while the text `UNREACHABLE` behind that comment is left
 *   untouched.
 */
class MoveBlackboxTest : AbstractBlackboxTest() {

    /**
     * Default behaviour: without a move command the `@template-renderer` comment stays after the
     * preamble, so the `<?xml ... ?>` preamble is not part of the generated output.
     */
    @Test
    fun `test output without moving the comment keeps the preamble out of the template`() {
        assertSameContent(
            webAppPath().resolve("move/move-default.xml"),
            webAppGeneratedPath().resolve("move/move-default.xml"),
            "move/move-default.expectation.xml",
        )
    }

    /**
     * `move-comment-backward` / `move-comment-forward`: the renderer comment is moved above the
     * preamble (so the preamble is rendered) and further comments are moved into the middle of tags
     * and across a normal comment, while never crossing the next typical-template comment.
     */
    @Test
    fun `test output of move-comment-backward and move-comment-forward commands`() {
        assertSameContent(
            webAppPath().resolve("move/move.xml"),
            webAppGeneratedPath().resolve("move/move.xml"),
            "move/move.expectation.xml",
        )
    }
}
