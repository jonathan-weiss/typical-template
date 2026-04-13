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
}

tasks.register<JavaExec>("generateDocumentation") {
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.codeblessing.typicaltemplate.documentation.MarkdownCreatorMainKt")

    val outputFile = rootProject.file("COMMAND-REFERENCE.md")
    outputs.file(outputFile)

    doFirst {
        standardOutput = outputFile.outputStream()
    }
}
