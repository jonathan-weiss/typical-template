package org.codeblessing.typicaltemplate.filesearch

import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

data class FoundFile(
    val filePath: Path,
    val rootDirectory: Path,
) {
    fun relativeToRootDirectory(): String {
        return filePath.relativeTo(rootDirectory).normalize().pathString
    }
}
