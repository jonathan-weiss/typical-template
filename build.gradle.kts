tasks.register("build") {
    dependsOn("generateDocumentation")
}

tasks.register("generateDocumentation") {
    dependsOn("updateVersionString")
}

tasks.register("updateVersionString") {
    val readmeMdFile: File = rootProject.file("README.md")
    val newVersion = project.property("tavnit.version") as String

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
        .replace(Regex("tavnit-api:\\d+\\.\\d+\\.\\d+"), "tavnit-api:$newVersion")
        .replace(Regex("tavnit:\\d+\\.\\d+\\.\\d+"), "tavnit:$newVersion")

    file.writeText(newContent)
}
