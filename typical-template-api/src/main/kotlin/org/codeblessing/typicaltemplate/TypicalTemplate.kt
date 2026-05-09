package org.codeblessing.typicaltemplate

import java.nio.file.Paths

val MAIN_FUNCTION_USAGE = """
Usage: <typical-template> --template-renderer <path> --search <path>:<pattern> [--search <path>:<pattern> ...]

Options:
  --template-renderer <path>   Target base directory for generated renderer classes (required)
  --search <path>:<pattern>    Source directory and filename glob to search, e.g. ./src:*.kt (required, repeatable)
  --help                       Show this help message

Examples:
  <typical-template> --template-renderer ./src/generated --search ./src/main/kotlin:*.kt
  <typical-template> --template-renderer ./src/generated --search ./src/main/kotlin:*.kt --search ./src/main/resources:*.html
  
Where <typical-template> is:    
    java -cp ./typical-template-api.jar:./typical-template.jar:${'$'}KOTLIN_HOME/lib/kotlin-stdlib.jar org.codeblessing.typicaltemplate.TypicalTemplateKt
or 
    kotlin -classpath ./typical-template-api.jar:./typical-template.jar org.codeblessing.typicaltemplate.TypicalTemplateKt
""".trimIndent()

fun main(args: Array<String>) {
    if (args.isEmpty() || args.contains("--help")) {
        println(MAIN_FUNCTION_USAGE)
        return
    }

    val templateRenderPath = parseFlag(args, "--template-renderer")
        ?: error("Missing required argument: --template-renderer <path>\n\nRun with --help for usage.")

    val searchValues = parseRepeatingFlag(args, "--search")
    if (searchValues.isEmpty()) error("Missing required argument: --search <path>:<pattern>\n\nRun with --help for usage.")

    val fileSearchLocations = searchValues.map { value ->
        val colonIndex = value.lastIndexOf(':')
        if (colonIndex <= 0) error("Invalid --search value '$value': expected <path>:<pattern>")
        val searchPath = value.substring(0, colonIndex)
        val globPattern = value.substring(colonIndex + 1)
        FileSearchLocation(
            rootDirectoryToSearch = Paths.get(searchPath),
            filenameMatchingPattern = globToRegex(globPattern),
        )
    }

    val configuration = TemplatingConfiguration(
        fileSearchLocations = fileSearchLocations,
        templateRendererConfiguration = TemplateRendererConfiguration(
            templateRendererTargetSourceBasePath = Paths.get(templateRenderPath),
        ),
    )

    val results = TypicalTemplateApi.runTypicalTemplate(listOf(configuration))
    results.values.flatten().forEach { println(it) }
}

private fun parseFlag(args: Array<String>, flag: String): String? {
    val index = args.indexOf(flag)
    return if (index >= 0 && index + 1 < args.size) args[index + 1] else null
}

private fun parseRepeatingFlag(args: Array<String>, flag: String): List<String> {
    val result = mutableListOf<String>()
    var i = 0
    while (i < args.size) {
        if (args[i] == flag && i + 1 < args.size) {
            result += args[i + 1]
            i += 2
        } else {
            i++
        }
    }
    return result
}

private fun globToRegex(glob: String): Regex {
    val regex = buildString {
        for (ch in glob) {
            when (ch) {
                '*' -> append(".*")
                '.' -> append("\\.")
                '?' -> append(".")
                else -> append(Regex.escape(ch.toString()))
            }
        }
        append("$")
    }
    return Regex(regex)
}
