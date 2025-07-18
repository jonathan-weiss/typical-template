plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
}

val directoryForTemplateRendererGeneratedSource = "src/generated/kotlin"

kotlin {
    sourceSets["main"].kotlin.srcDir(directoryForTemplateRendererGeneratedSource)
}

tasks.register<Delete>("cleanGeneratedSource") {
    delete(file(directoryForTemplateRendererGeneratedSource))
}

tasks.named("clean") {
    dependsOn("cleanGeneratedSource")
}
