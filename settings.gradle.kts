pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.emad.dev/repository/maven-public/")
        }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://maven.emad.dev/repository/maven-public/")
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "Test"
include(":app")
 