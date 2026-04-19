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
    mainClass.set("org.codeblessing.typicaltemplate.TypicalTemplateKt")

    val pathToKotlinSourceFiles = exampleBusinessProjectPath.resolve("src/main/kotlin").absolutePath
    val pathToHtmlSourceFiles = exampleBusinessProjectPath.resolve("src/webapp").absolutePath
    args(
        "--template-render",
        targetDirectoryForTemplateRenderer.absolutePath,  // Path to the base directory where the template renderers are written to
        "--search",
        "$pathToKotlinSourceFiles:*.kt",
        "--search",
        "$pathToHtmlSourceFiles:*.html",
    )
}
