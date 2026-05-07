plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-dependency-repository`
}

val directoryForTemplateRendererGeneratedSource = "src/generated/kotlin"

kotlin {
    sourceSets["main"].kotlin.srcDir(directoryForTemplateRendererGeneratedSource)
}

tasks.register<Delete>("cleanGeneratedSource") {
    delete(file(directoryForTemplateRendererGeneratedSource))
}

tasks.clean {
    dependsOn("cleanGeneratedSource")
}

val taskNameExecuteTypicalTemplateRenderers = ":typical-template-blackbox-tests:template-renderer-executor:executeTypicalTemplateRenderers"
tasks.compileKotlin {
    dependsOn(taskNameExecuteTypicalTemplateRenderers)
}
