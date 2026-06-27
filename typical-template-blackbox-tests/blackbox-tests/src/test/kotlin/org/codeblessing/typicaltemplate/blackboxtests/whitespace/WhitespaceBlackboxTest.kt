package org.codeblessing.typicaltemplate.blackboxtests.whitespace

import org.codeblessing.typicaltemplate.blackboxtests.AbstractBlackboxTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

/**
 * Blackbox tests for the whitespace-handling commands around typical-template comments:
 * - `remove-blanks-before-comment` / `remove-blanks-after-comment`
 * - `remove-blanks-and-linebreak-before-comment` / `remove-blanks-and-linebreak-after-comment`
 * - `keep-blanks-and-linebreak-before-comment` / `keep-blanks-and-linebreak-after-comment`
 * - the default whitespace handling that applies when none of those commands is used.
 *
 * The same set of commands is exercised once in an HTML file (free `<!-- -->` block comment) and once
 * in a Kotlin file (`/* */` block comment) to show that the behaviour is independent of the comment
 * style. The test is parameterized over both comment languages via [WhitespaceTemplateLanguage].
 *
 * Each template marks the relevant text with `keepA`/`keepB` so that the effect of every command is
 * directly visible in the generated output:
 * - default: a comment that stands alone on its line has the blanks before it and the blanks plus the
 *   line break after it collapsed (before and after side are decided independently).
 * - `remove-blanks(-and-linebreak)-before/after`: additionally strip the blanks (and optionally the
 *   line break) on the respective side.
 * - `keep-blanks-and-linebreak-before/after`: suppress the default collapsing on the respective side.
 */
class WhitespaceBlackboxTest : AbstractBlackboxTest() {

    @ParameterizedTest(name = "whitespace commands behave identically in {0}")
    @EnumSource(WhitespaceTemplateLanguage::class)
    fun `whitespace commands produce the expected output`(language: WhitespaceTemplateLanguage) {
        val templateRoot = when (language) {
            WhitespaceTemplateLanguage.HTML -> webAppPath()
            WhitespaceTemplateLanguage.KOTLIN -> kotlinSourcePath()
        }
        assertSameContent(
            templateRoot.resolve(language.templateRelativePath),
            webAppGeneratedPath().resolve(language.generatedRelativePath),
            language.expectationResource,
        )
    }

    /**
     * The two comment languages the whitespace behaviour is demonstrated in. Only the comment style and
     * the carrier syntax differ between the templates; both exercise exactly the same commands.
     */
    enum class WhitespaceTemplateLanguage(
        val templateRelativePath: String,
        val generatedRelativePath: String,
        val expectationResource: String,
    ) {
        HTML(
            templateRelativePath = "whitespace/whitespace.html",
            generatedRelativePath = "whitespace/whitespace.html",
            expectationResource = "whitespace/whitespace.expectation.html",
        ),
        KOTLIN(
            templateRelativePath = "my/example/businessproject/whitespace/Whitespace.kt",
            generatedRelativePath = "whitespace/whitespace.kt",
            expectationResource = "whitespace/whitespace.expectation.kt",
        ),
    }
}
