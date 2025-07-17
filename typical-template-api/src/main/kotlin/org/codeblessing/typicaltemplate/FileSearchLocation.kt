package org.codeblessing.typicaltemplate

import java.nio.file.Path

data class FileSearchLocation(
    val rootDirectoryToSearch: Path,
    val filenameMatchingPattern: Regex,
)
