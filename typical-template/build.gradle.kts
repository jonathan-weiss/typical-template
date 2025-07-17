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
    testImplementation(kotlin("test"))
}
