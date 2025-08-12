plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-dependency-repository`
}

dependencies {
    implementation(project(":typical-template-api"))
    runtimeOnly(project(":typical-template"))
}

tasks.register<JavaExec>("createTypicalTemplateRenderers") {
    val exampleBusinessProjectPath = project(":typical-template-full-process-example:example-business-project").projectDir
    val templateRendererExecutorPath = project(":typical-template-full-process-example:template-renderer-executor").projectDir
    val targetDirectoryForTemplateRenderer = templateRendererExecutorPath.resolve("src/typicaltemplate-generated/kotlin")

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.codeblessing.typicaltemplate.example.TypicalTemplateRendererCreatorKt")

    args(
        exampleBusinessProjectPath.resolve("src/main/kotlin").absolutePath, // First argument: Path to the directory within the kotlin template files are searched
        exampleBusinessProjectPath.resolve("src/webapp").absolutePath, // Second argument: Path to the directory within the HTML template files are searched
        targetDirectoryForTemplateRenderer.absolutePath  // Third argument: Path to the base directory where the template renderers are written to
    )
}
