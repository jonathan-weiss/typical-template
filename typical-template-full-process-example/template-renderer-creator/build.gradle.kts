plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
}

kotlin {
    sourceSets["main"].kotlin.srcDir("src/example/kotlin")
}


dependencies {
    implementation(project(":typical-template-api"))
    runtimeOnly(project(":typical-template"))
}

tasks.register<JavaExec>("createTypicalTemplateRenderers") {
    val templateRendererExecutorPath = project(":typical-template-full-process-example:template-renderer-executor").projectDir
    val targetDirectoryForTemplateRenderer = templateRendererExecutorPath.resolve("src/typicaltemplate/kotlin")

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.codeblessing.typicaltemplate.example.TypicalTemplateRendererCreatorKt")

    args(
        projectDir.resolve("src/example/kotlin").absolutePath, // First argument: Path to source the directory within the template files are searched
        targetDirectoryForTemplateRenderer.absolutePath  // Second argument: Path to the base directory where the template renderers are written to
    )
}
