plugins {
    alias(libs.plugins.kotlin.jvm)
    `typical-template-publishing`
    `maven-dependency-repository`
}

tasks.test {
    useJUnitPlatform()
}
dependencies {
    implementation(project(":typical-template-api"))

    testImplementation(kotlin("test"))
}
