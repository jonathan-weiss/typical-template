plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-dependency-repository`
}

val directoryForGeneratedTemplateRenderer = "src/typicaltemplate-generated/kotlin"
val directoryForTemplateRendererGeneratedKotlinSource = "src/generated/kotlin"
val directoryForTemplateRendererGeneratedHtmlSource = "src/webapp-generated"
val taskNameCreateTypicalTemplateRenderers = ":typical-template-blackbox-tests:template-renderer-creator:createTypicalTemplateRenderers"

val exampleBusinessProjectPath: File = project(":typical-template-blackbox-tests:example-business-project").projectDir
kotlin {
    sourceSets["main"].kotlin.srcDir(directoryForGeneratedTemplateRenderer)
}

tasks.register<JavaExec>("executeTypicalTemplateRenderers") {
    dependsOn(taskNameCreateTypicalTemplateRenderers)
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.codeblessing.typicaltemplate.example.TypicalTemplateRendererExecutorKt")

    val generatedKotlinSourceDir = exampleBusinessProjectPath.resolve(directoryForTemplateRendererGeneratedKotlinSource)
    val generatedHtmlSourceDir = exampleBusinessProjectPath.resolve(directoryForTemplateRendererGeneratedHtmlSource)


    args(
        generatedKotlinSourceDir,
        generatedHtmlSourceDir
    )

    outputs.dir(generatedKotlinSourceDir)
    outputs.dir(generatedHtmlSourceDir)
}

tasks.compileKotlin {
    dependsOn(taskNameCreateTypicalTemplateRenderers)
}

tasks.register("example") { // easier to remember and shorter to type
    dependsOn("executeTypicalTemplateRenderers")
}

