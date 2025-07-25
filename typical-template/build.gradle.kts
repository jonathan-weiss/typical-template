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

    testImplementation(kotlin("test"))
}
