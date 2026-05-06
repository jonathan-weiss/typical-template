plugins {
    alias(libs.plugins.kotlin.jvm)
    `typical-template-publishing`
    `maven-dependency-repository`
}

tasks.test {
    useJUnitPlatform()
}
dependencies {
    implementation(project(":typical-template-api"))

    testImplementation(kotlin("test"))
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}
tasks.register("generateDocumentation") {
    dependsOn("generateCommandReferenceDocumentation")
    dependsOn("generateMainFunctionUsageDocumentation")
}

tasks.register<JavaExec>("generateMainFunctionUsageDocumentation") {
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.codeblessing.typicaltemplate.documentation.MainFunctionMarkdownCreatorMainKt")

    val outputFile = rootProject.file("MAIN-FUNCTION-USAGE.md")
    outputs.file(outputFile)

    doFirst {
        standardOutput = outputFile.outputStream()
    }
}

tasks.register<JavaExec>("generateCommandReferenceDocumentation") {
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.codeblessing.typicaltemplate.documentation.CommandReferenceMarkdownCreatorMainKt")

    val outputFile = rootProject.file("COMMAND-REFERENCE.md")
    outputs.file(outputFile)

    doFirst {
        standardOutput = outputFile.outputStream()
    }
}
