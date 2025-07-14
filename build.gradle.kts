plugins {
    kotlin("jvm") version "2.2.0"
    application
}

repositories {
    mavenCentral()
}

tasks.named("run") {
    enabled = true
}

application {
    mainClass.set("org.codeblessing.typicaltemplate.TypicalTemplateKt")
}

tasks.register("templater") {
    dependsOn("run")
}

tasks.test {
    useJUnitPlatform()
}
dependencies {
    testImplementation(kotlin("test"))
}
