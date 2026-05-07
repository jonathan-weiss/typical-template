tasks.register("build") {
    dependsOn("generateDocumentation")
}

tasks.register("generateDocumentation") {
    dependsOn("updateVersionString")
}

tasks.register("updateVersionString") {
    val readmeMdFile: File = rootProject.file("README.md")
    val newVersion = project.property("typicaltemplate.version") as String

    doLast {
        replaceVersionString(readmeMdFile, newVersion)
    }

    inputs.property("version", newVersion)
    inputs.file(readmeMdFile)
    outputs.file(readmeMdFile)
}

fun replaceVersionString(file: File, newVersion: String) {
    val content = file.readText()
    val newContent = content
        .replace(Regex("typical-template-api:\\d+\\.\\d+\\.\\d+"), "typical-template-api:$newVersion")
        .replace(Regex("typical-template:\\d+\\.\\d+\\.\\d+"), "typical-template:$newVersion")

    file.writeText(newContent)
}
