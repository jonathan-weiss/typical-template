package org.codeblessing.typicaltemplate.blackboxtests

import org.junit.jupiter.api.Test

/**
 * Blackbox tests for the commands that control the whitespace around typical-template comments:
 * - remove-blanks-before-comment / remove-blanks-after-comment
 * - remove-blanks-and-linebreak-before-comment / remove-blanks-and-linebreak-after-comment
 *
 * The templates use `keepA`/`keepB` markers around the comments so that the effect of each command
 * is directly visible in the generated output. They also cover the case where several
 * typical-template comments follow each other directly.
 */
class WhitespaceCommandsBlackboxTest: AbstractBlackboxTest() {

    /**
     * `remove-blanks(-and-linebreak)-before/after-comment` strip the blanks (and optionally the
     * line break) on the respective side of the comment, so `keepA` and `keepB` end up joined.
     */
    @Test
    fun `test output of remove-blanks commands`() {
        assertSameContent(
            webAppPath().resolve("whitespace-remove.html"),
            webAppGeneratedPath().resolve("whitespace-remove.html"),
            "whitespace-remove.expectation.html")
    }

    /**
     * Several typical-template comments directly in a row are each handled on their own:
     * two consecutive `remove-blanks-and-linebreak-after-comment` comments merge the lines.
     */
    @Test
    fun `test output of consecutive comments`() {
        assertSameContent(
            webAppPath().resolve("whitespace-consecutive.html"),
            webAppGeneratedPath().resolve("whitespace-consecutive.html"),
            "whitespace-consecutive.expectation.html")
    }
}
