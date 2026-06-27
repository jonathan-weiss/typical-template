package org.codeblessing.typicaltemplate.blackboxtests.nesting

import org.codeblessing.typicaltemplate.blackboxtests.AbstractBlackboxTest
import org.junit.jupiter.api.Test

/**
 * Blackbox test for the nesting of commands and for the auto-closing of commands at the boundary to
 * the next-higher nesting level.
 *
 * The template nests `if` inside `foreach` inside two `replace-value-by-*` commands and additionally
 * uses an `ignore-text` block. It shows that:
 * - the commands nest correctly: every list entry is rendered through `if` inside `foreach`, with both
 *   replacements applied (`MARK` -> `*`, `ENTRY` -> the loop variable);
 * - the two replacements are auto-closed when their enclosing `foreach` is closed: the line
 *   `tail: MARK ENTRY` after `end-foreach` keeps `MARK` and `ENTRY` literally because the replacements
 *   no longer apply at that (higher) nesting level;
 * - the `ignore-text` block has no explicit `end-ignore-text`; it is auto-closed by the
 *   `end-template-renderer`, so its body does not appear in the output.
 */
class NestingBlackboxTest : AbstractBlackboxTest() {

    @Test
    fun `test command nesting and autoclose at the boundary to the next-higher level`() {
        assertSameContent(
            webAppPath().resolve("nesting/nesting.html"),
            webAppGeneratedPath().resolve("nesting/nesting.html"),
            "nesting/nesting.expectation.html",
        )
    }
}
