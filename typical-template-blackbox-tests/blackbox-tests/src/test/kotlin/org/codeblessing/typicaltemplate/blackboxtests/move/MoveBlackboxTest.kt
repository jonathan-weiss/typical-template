package org.codeblessing.typicaltemplate.blackboxtests.move

import org.codeblessing.typicaltemplate.blackboxtests.AbstractBlackboxTest
import org.junit.jupiter.api.Test

/**
 * Blackbox tests for `move-comment-backward` (`@mvb`) and `move-comment-forward` (`@mvf`).
 *
 * The templates are HTML documents that start with the `<!DOCTYPE html>` declaration. Everything in
 * front of the first `@template-renderer` comment is excluded from the output, so the position of that
 * comment relative to the doctype decides whether the doctype is rendered. The tests show the two
 * extremes:
 * - [move-default.html] keeps the `@template-renderer` comment where it is written (after the
 *   doctype), so the `<!DOCTYPE html>` declaration is *not* part of the template output.
 * - [move.html] uses `@move-comment-backward` on the very same comment to move it above the doctype,
 *   so the `<!DOCTYPE html>` declaration becomes part of the rendered output.
 *
 * [move.html] additionally demonstrates the remaining behaviour described in the command reference:
 * - moving a comment into the middle of an HTML tag to *insert* an attribute (`@mvb` + `@print-text`),
 * - moving a comment into the middle of an element to *ignore* a child element (`@mvf` + `@ignore-text`),
 * - that a move travels across a normal (non-typical-template) HTML comment, and
 * - that a move never crosses the next typical-template comment: `(last)` is inserted at the last
 *   comma *before* the following comment while the text `UNREACHABLE` behind that comment is left
 *   untouched.
 */
class MoveBlackboxTest : AbstractBlackboxTest() {

    /**
     * Default behaviour: without a move command the `@template-renderer` comment stays after the
     * doctype, so the `<!DOCTYPE html>` declaration is not part of the generated output.
     */
    @Test
    fun `test output without moving the comment keeps the doctype out of the template`() {
        assertSameContent(
            webAppPath().resolve("move/move-default.html"),
            webAppGeneratedPath().resolve("move/move-default.html"),
            "move/move-default.expectation.html",
        )
    }

    /**
     * `move-comment-backward` / `move-comment-forward`: the renderer comment is moved above the
     * doctype (so the doctype is rendered) and further comments are moved into the middle of tags
     * and across a normal comment, while never crossing the next typical-template comment.
     */
    @Test
    fun `test output of move-comment-backward and move-comment-forward commands`() {
        assertSameContent(
            webAppPath().resolve("move/move.html"),
            webAppGeneratedPath().resolve("move/move.html"),
            "move/move.expectation.html",
        )
    }
}
