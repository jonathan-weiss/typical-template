plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-dependency-repository`
}

dependencies {
    implementation(project(":typical-template-api"))
    // The implementation module is put additionally on the task classpath as a packaged jar (see
    // createTypicalTemplateRenderers below), not as a project dependency. Gradle (9.x) resolves
    // project dependencies on the runtime classpath via the loose classes/resources directory
    // variants, whose META-INF/services output can go stale and make the ServiceLoader lookup fail.
    runtimeOnly(project(":typical-template"))
}

tasks.register<JavaExec>("createTypicalTemplateRenderers") {
    val exampleBusinessProjectPath = project(":typical-template-blackbox-tests:example-business-project").projectDir
    val templateRendererExecutorPath = project(":typical-template-blackbox-tests:template-renderer-executor").projectDir
    val targetDirectoryForTemplateRenderer = templateRendererExecutorPath.resolve("src/typicaltemplate-generated/kotlin")

    // Consume the packaged jar of the implementation module so its ServiceLoader registration
    // (META-INF/services/...TypicalTemplateProcessorApi) always travels atomically with the
    // classes. This avoids the intermittent "Could not find an implementation of
    // TypicalTemplateProcessorApi" failure caused by a stale loose resources directory.
    val implementationJar = project(":typical-template").tasks.named<Jar>("jar")
    dependsOn(implementationJar)

    classpath = sourceSets.main.get().runtimeClasspath + files(implementationJar)
    mainClass.set("org.codeblessing.typicaltemplate.TypicalTemplateKt")

    val pathToKotlinSourceFiles = exampleBusinessProjectPath.resolve("src/main/kotlin")
    val pathToHtmlSourceFiles = exampleBusinessProjectPath.resolve("src/main/resources/html")
    args(
        "--template-renderer",
        targetDirectoryForTemplateRenderer.absolutePath,  // Path to the base directory where the template renderers are written to
        "--search",
        "${pathToKotlinSourceFiles.absolutePath}:*.kt",
        "--search",
        "${pathToHtmlSourceFiles.absolutePath}:*.html",
    )

    inputs.dir(pathToKotlinSourceFiles)
    inputs.dir(pathToHtmlSourceFiles)
    outputs.dir(targetDirectoryForTemplateRenderer)
}
