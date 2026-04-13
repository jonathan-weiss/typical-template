package org.codeblessing.typicaltemplate

import java.nio.file.Paths
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.writeText

object ClasspathResourceWriter {
    private const val RESOURCE_DIRECTORY = "src/test/resources/"

    fun writeClasspathResource(classpathResourcePath: String, text: String) {
        val classpathResourceFile = Paths.get(RESOURCE_DIRECTORY + classpathResourcePath)
        if(!classpathResourceFile.exists()) {
            throw RuntimeException("Classpath resource file does not exist. Create an empty file. \n${classpathResourceFile.absolutePathString()}")
        } else {
            classpathResourceFile.writeText(text)
        }
    }
}
