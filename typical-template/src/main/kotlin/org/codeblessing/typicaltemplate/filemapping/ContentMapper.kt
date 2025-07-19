package org.codeblessing.typicaltemplate.filemapping

import org.codeblessing.typicaltemplate.CommentStyle
import org.codeblessing.typicaltemplate.filemapping.CommentStyles.HTML_COMMENT_STYLES
import org.codeblessing.typicaltemplate.filemapping.CommentStyles.KOTLIN_COMMENT_STYLES
import org.codeblessing.typicaltemplate.filemapping.FileEndings.HTML_FILENAME_REGEX
import org.codeblessing.typicaltemplate.filemapping.FileEndings.KOTLIN_FILENAME_REGEX
import java.nio.file.Path
import kotlin.io.path.name

object ContentMapper {

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
