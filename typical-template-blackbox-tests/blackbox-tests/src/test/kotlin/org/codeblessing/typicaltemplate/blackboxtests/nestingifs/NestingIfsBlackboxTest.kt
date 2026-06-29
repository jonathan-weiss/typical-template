package org.codeblessing.typicaltemplate.blackboxtests.nestingifs

import org.codeblessing.typicaltemplate.blackboxtests.AbstractBlackboxTest
import org.junit.jupiter.api.Test

/**
 * Blackbox test for nested `if` with `else-if` and/or `else`.
 *
 * The template has an outer `if` (`isBook`) / `else-if` (`isMovie`) / `else` chain and, inside the
 * `book` branch, a nested `if` (`highlighted`) / `else`. The renderer is invoked once for the input
 * that selects each branch and the results are concatenated into one labelled output file, so the test
 * checks every path through the nested conditionals at once:
 * - `book + highlighted`   -> `<li class="book">` + `<li class="badge">highlighted` (outer `if`, nested `if`)
 * - `book + not highlighted` -> `<li class="book">` + `<li class="badge">plain` (outer `if`, nested `else`)
 * - `movie`                -> `<li class="movie">` (outer `else-if`)
 * - `neither`              -> `<li class="other">` (outer `else`)
 */
class NestingIfsBlackboxTest : AbstractBlackboxTest() {

    @Test
    fun `test that nested if with else-if and else selects the correct branch`() {
        assertSameContent(
            webAppPath().resolve("nestingifs/nesting-ifs.html"),
            webAppGeneratedPath().resolve("nestingifs/nesting-ifs.html"),
            "nestingifs/nesting-ifs.expectation.html",
        )
    }
}
