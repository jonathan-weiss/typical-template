package org.codeblessing.typicaltemplate.blackboxtests.nestingreplaces

import org.codeblessing.typicaltemplate.blackboxtests.AbstractBlackboxTest
import org.junit.jupiter.api.Test

/**
 * Blackbox test for nested `replace-value-by-value` / `replace-value-by-expression`.
 *
 * The replacements are applied in the order they are listed and from the innermost (most nested)
 * to the outermost replacement. The chosen tokens make the ordering observable in the result:
 * - `within-comment-order: ONE` becomes `THREE`: the outer comment lists `ONE -> TWO` before
 *   `TWO -> THREE`, and they are applied in that listed order (`ONE` -> `TWO` -> `THREE`). A different
 *   order would have produced `TWO`.
 * - `innermost-first: ZERO` becomes `THREE`: the inner replacement `ZERO -> ONE` runs first, then the
 *   outer chain turns `ONE` into `THREE`. If the outer replacements ran first, `ZERO` would have stayed
 *   `ZERO` and only become `ONE` afterwards.
 * - `expression: EXPRTOKEN` becomes `42`: a nested `replace-value-by-expression` participates in the
 *   same nesting (`EXPRTOKEN` -> `40 + 2`).
 * - `after-inner: ZERO` stays `ZERO`: once the inner replacement is closed, only the outer chain
 *   remains, which does not match `ZERO`.
 */
class NestingReplacesBlackboxTest : AbstractBlackboxTest() {

    @Test
    fun `test that nested replacements apply in listing order and from innermost to outermost`() {
        assertSameContent(
            htmlSourcePath().resolve("nestingreplaces/nesting-replaces.html"),
            htmlGeneratedPath().resolve("nestingreplaces/nesting-replaces.html"),
            "nestingreplaces/nesting-replaces.expectation.html",
        )
    }
}
