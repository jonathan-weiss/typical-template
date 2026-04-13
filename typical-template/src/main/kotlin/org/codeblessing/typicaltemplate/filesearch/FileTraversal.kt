package org.codeblessing.typicaltemplate.filesearch

import org.codeblessing.typicaltemplate.FileSearchLocation
import org.codeblessing.typicaltemplate.RelativeFile
import java.nio.file.Path
import kotlin.io.path.name


object FileTraversal {
    fun searchFiles(fileSearchLocations: List<FileSearchLocation>): List<RelativeFile> {
        return fileSearchLocations
            .flatMap { fileSearchLocation -> searchRecursivelyInFileLocation(fileSearchLocation) }
    }

    private fun searchRecursivelyInFileLocation(fileSearchLocation: FileSearchLocation): List<RelativeFile> {
        return walkTopDown(
            rootDirectory = fileSearchLocation.rootDirectoryToSearch,
            filenameMatchingPattern = fileSearchLocation.filenameMatchingPattern
        )
    }

    private fun walkTopDown(rootDirectory: Path, filenameMatchingPattern: Regex): List<RelativeFile> {
        return rootDirectory
            .toFile()
            .walkTopDown()
            .filter { it.isFile }
            .map { it.toPath() }
            .filter { isFileMatching(it, filenameMatchingPattern) }
            .map { RelativeFile(filePath = it, rootDirectory = rootDirectory,) }
            .toList()
    }

    private fun isFileMatching(file: Path, filenamePattern: Regex): Boolean {
        return filenamePattern.matches(file.name)
    }
}
