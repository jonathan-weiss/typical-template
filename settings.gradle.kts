rootProject.name = "typical-template"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val kotlinVersion = version("kotlin", "2.2.0")

            library("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib").versionRef(kotlinVersion)
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef(kotlinVersion)
        }
    }
}


include("typical-template")
include("typical-template-api")
include("typical-template-full-process-example:template-renderer-creator")
include("typical-template-full-process-example:template-renderer-executor")
include("typical-template-full-process-example:example-business-project")
