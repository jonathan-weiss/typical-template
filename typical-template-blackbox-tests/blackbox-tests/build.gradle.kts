plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-dependency-repository`
}

val blackboxBaseProject = project(":typical-template-blackbox-tests:example-business-project")
tasks.test {
    useJUnitPlatform()
    systemProperty("blackbox.baseProjectPath", blackboxBaseProject.projectDir.absolutePath)

    // we need to depend on the compiled example project,
    // which triggers the template renderer execution
    // which triggers the template renderer creation
    dependsOn("${blackboxBaseProject.path}:compileKotlin")
}
dependencies {
    implementation(project(":typical-template-api"))

    testImplementation(kotlin("test"))

    testImplementation("org.junit.jupiter:junit-jupiter-params")
}
