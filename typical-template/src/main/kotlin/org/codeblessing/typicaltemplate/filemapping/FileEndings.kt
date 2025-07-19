package org.codeblessing.typicaltemplate.filemapping

object FileEndings {
    val HTML_FILENAME_REGEX: Regex = Regex(".*\\.(html|xhtml)")
    val KOTLIN_FILENAME_REGEX: Regex = Regex(".*\\.kt")
}
