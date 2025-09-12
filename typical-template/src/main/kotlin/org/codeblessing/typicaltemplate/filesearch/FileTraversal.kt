package org.codeblessing.typicaltemplate.filesearch

import org.codeblessing.typicaltemplate.FileSearchLocation
import java.nio.file.Path
import kotlin.collections.filter
import kotlin.io.path.name
import kotlin.io.path.relativeTo


object FileTraversal {
    fun searchFiles(fileSearchLocations: List<FileSearchLocation>): List<FoundFile> {
        return fileSearchLocations
            .flatMap { fileSearchLocation -> searchRecursivelyInFileLocation(fileSearchLocation) }
    }

    private fun searchRecursivelyInFileLocation(fileSearchLocation: FileSearchLocation): List<FoundFile> {
        return walkTopDown(
            rootDirectory = fileSearchLocation.rootDirectoryToSearch,
            filenameMatchingPattern = fileSearchLocation.filenameMatchingPattern
        )
    }

    private fun walkTopDown(rootDirectory: Path, filenameMatchingPattern: Regex): List<FoundFile> {
        return rootDirectory
            .toFile()
            .walkTopDown()
            .filter { it.isFile }
            .map { it.toPath() }
            .filter { isFileMatching(it, filenameMatchingPattern) }
            .map { FoundFile(filePath = it, rootDirectory = rootDirectory, ) }
            .toList()
    }

    private fun isFileMatching(file: Path, filenamePattern: Regex): Boolean {
        return filenamePattern.matches(file.name)
    }
}
