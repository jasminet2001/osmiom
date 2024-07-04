pluginManagement {
    repositories {
        maven {

            url=uri("https://maven.emad.dev/repository/maven-public/")

        }
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
        maven {

            url=uri("https://maven.emad.dev/repository/maven-public/")

        }
        google()
        mavenCentral()
    }
}

rootProject.name = "My Application1"
include(":app")
 