package org.codeblessing.tavnit

import java.nio.file.Path

data class FileSearchLocation(
    val rootDirectoryToSearch: Path,
    val filenameMatchingPattern: Regex,
)
