plugins {
    alias(libs.plugins.kotlin.jvm)
    `typical-template-publishing`
    `maven-dependency-repository`
}

tasks.jar {
    val typicalTemplateVersion = project.property("typicaltemplate.version") as String
    manifest {
        attributes("Main-Class" to "org.codeblessing.typicaltemplate.TypicalTemplateKt")
        attributes("Implementation-Version" to typicalTemplateVersion)
    }
}
