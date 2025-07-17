package org.codeblessing.typicaltemplate.filesearch

import java.nio.file.Path

class FileSearchLocation(
    val rootDirectoryToSearch: Path,
    val filenameMatchingPattern: Regex,
)
