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
include("typical-template-blackbox-tests:blackbox-tests")
include("typical-template-blackbox-tests:template-renderer-creator")
include("typical-template-blackbox-tests:template-renderer-executor")
include("typical-template-blackbox-tests:example-business-project")
