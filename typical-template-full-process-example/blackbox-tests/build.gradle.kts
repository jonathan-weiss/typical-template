plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-dependency-repository`
}

tasks.test {
    val blackboxBaseProject = project(":typical-template-full-process-example:example-business-project")
    useJUnitPlatform()
    systemProperty("blackbox.baseProjectPath", blackboxBaseProject.projectDir.absolutePath)
}
dependencies {
    implementation(project(":typical-template-api"))

    testImplementation(kotlin("test"))
}
