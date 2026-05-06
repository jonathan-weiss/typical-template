
tasks.register("generateDocumentation") {
    dependsOn("updateVersionString")
}

tasks.register("updateVersionString") {
    val readmeMdFile: File = rootProject.file("README.md")

    doLast {
        replaceVersionString(readmeMdFile)
    }

    inputs.file(readmeMdFile)
    outputs.file(readmeMdFile)
}

fun replaceVersionString(file: File) {
    val version = project.property("typicaltemplate.version") as String
    val content = file.readText()
    val newContent = content
        .replace(Regex("typical-template-api:\\d+\\.\\d+\\.\\d+"), "typical-template-api:$version")
        .replace(Regex("typical-template:\\d+\\.\\d+\\.\\d+"), "typical-template:$version")

    file.writeText(newContent)
}
