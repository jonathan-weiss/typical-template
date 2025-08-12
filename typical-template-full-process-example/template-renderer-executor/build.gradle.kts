plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-dependency-repository`
}

val directoryForGeneratedTemplateRenderer = "src/typicaltemplate-generated/kotlin"
val directoryForTemplateRendererGeneratedKotlinSource = "src/generated/kotlin"
val directoryForTemplateRendererGeneratedHtmlSource = "src/webapp-generated"

val exampleBusinessProjectPath = project(":typical-template-full-process-example:example-business-project").projectDir
kotlin {
    sourceSets["main"].kotlin.srcDir(directoryForGeneratedTemplateRenderer)
}

dependencies {
    implementation(project(":typical-template-api"))
    runtimeOnly(project(":typical-template"))
}

tasks.register<JavaExec>("executeTypicalTemplateRenderers") {
    dependsOn(":typical-template-full-process-example:template-renderer-creator:createTypicalTemplateRenderers")
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.codeblessing.typicaltemplate.example.TypicalTemplateRendererExecutorKt")

    args(
        exampleBusinessProjectPath.resolve(directoryForTemplateRendererGeneratedKotlinSource),
        exampleBusinessProjectPath.resolve(directoryForTemplateRendererGeneratedHtmlSource)
    )
}

tasks.register("example") { // easier to remember and shorter to type
    dependsOn("executeTypicalTemplateRenderers")
}

