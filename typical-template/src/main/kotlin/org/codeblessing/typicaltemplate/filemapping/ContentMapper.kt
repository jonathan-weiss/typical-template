package org.codeblessing.typicaltemplate.filemapping

import org.codeblessing.typicaltemplate.CommentStyle
import org.codeblessing.typicaltemplate.config.TypicalTemplateConfigProvider
import java.nio.file.Path
import kotlin.io.path.extension

object ContentMapper {

    fun mapContent(file: Path): List<CommentStyle> {
        val configuration = TypicalTemplateConfigProvider.getConfiguration()
        val fileExtension = file.extension

        return configuration.fileExtensionCommentStyles
            .filter { fileExtension.equals(it.extension, ignoreCase = true) }
            .flatMap { it.commentStyles }
    }
}
