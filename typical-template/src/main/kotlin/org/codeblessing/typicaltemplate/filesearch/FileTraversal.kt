package org.codeblessing.typicaltemplate.filesearch

import java.nio.file.Path
import kotlin.io.path.name


object FileTraversal {
    fun searchFiles(fileSearchLocations: List<FileSearchLocation>): List<Path> {
        return fileSearchLocations
            .flatMap { fileSearchLocation -> searchRecursivelyInFileLocation(fileSearchLocation) }
    }

    private fun searchRecursivelyInFileLocation(fileSearchLocation: FileSearchLocation): List<Path> {
        return fileSearchLocation.rootDirectoryToSearch
            .toFile()
            .walkTopDown()
            .filter { it.isFile }
            .map { it.toPath() }
            .filter { isFileMatching(it, fileSearchLocation.filenameMatchingPattern) }
            .toList()
    }

    private fun isFileMatching(file: Path, filenamePattern: Regex): Boolean {
        return filenamePattern.matches(file.name)
    }
}
