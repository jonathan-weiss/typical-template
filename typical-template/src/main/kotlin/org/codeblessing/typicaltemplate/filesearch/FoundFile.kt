package org.codeblessing.typicaltemplate.filesearch

import java.nio.file.Path

data class FoundFile(
    val filePath: Path,
    val rootDirectory: Path,
)
