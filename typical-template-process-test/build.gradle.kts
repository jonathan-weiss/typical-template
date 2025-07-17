plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
dependencies {
    implementation(project(":typical-template-api"))
    runtimeOnly(project(":typical-template"))

    testImplementation(kotlin("test"))
}
