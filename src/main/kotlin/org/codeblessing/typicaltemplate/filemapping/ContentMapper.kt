package org.codeblessing.typicaltemplate.filemapping

import org.codeblessing.typicaltemplate.contentparsing.CommentStyle
import java.nio.file.Path
import kotlin.io.path.name

object ContentMapper {

    val HTML_FILENAME_REGEX: Regex = Regex(".*\\.(html|xhtml)")
    val HTML_COMMENT_STYLES = listOf(
        CommentStyle(
            startOfComment = "<!--",
            endOfComment = "-->"
        ),
    )
    val KOTLIN_FILENAME_REGEX: Regex = Regex(".*\\.kt")
    val KOTLIN_COMMENT_STYLES = listOf(
        CommentStyle(
            startOfComment = "/*",
            endOfComment = "*/"
        ),
        CommentStyle(
            startOfComment = "//",
            endOfComment = "\r\n",
            includeEndCommentInContent = true
        ),
        CommentStyle(
            startOfComment = "//",
            endOfComment = "\n",
            includeEndCommentInContent = true
        ),
        CommentStyle(
            startOfComment = "//",
            endOfComment = "\r",
            includeEndCommentInContent = true
        ),
    )

    fun mapContent(file: Path): List<CommentStyle> {
        if(HTML_FILENAME_REGEX.matches(file.name)) {
            return HTML_COMMENT_STYLES
        }
        if(KOTLIN_FILENAME_REGEX.matches(file.name)) {
            return KOTLIN_COMMENT_STYLES
        }
        return emptyList()
    }
}
