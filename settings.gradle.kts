enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "kotlin-study-murjune"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("kotlin-practice")
include("design-pattern")
include("coroutine")
include(":compose-practice")
include(":compose-ui-practice")
include(":algorithm-practice")
include(":java-practice")
