plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
}

val directoryForGeneratedTemplateRenderer = "src/typicaltemplate/kotlin"
val directoryForTemplateRendererGeneratedSource = "src/generated/kotlin"

kotlin {
    sourceSets["main"].kotlin.srcDir(directoryForGeneratedTemplateRenderer)
    sourceSets["main"].kotlin.srcDir(directoryForTemplateRendererGeneratedSource)
}

tasks.register<Delete>("cleanGeneratedSource") {
    delete(file(directoryForTemplateRendererGeneratedSource))
}

tasks.named("clean") {
    dependsOn("cleanGeneratedSource")
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
        project.projectDir.resolve(directoryForTemplateRendererGeneratedSource)
    )
}

tasks.register("example") { // easier to remember and shorter to type
    dependsOn("executeTypicalTemplateRenderers")
}

