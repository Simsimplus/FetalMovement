pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.40.2"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://maven.pkg.jetbrains.space/data2viz/p/maven/dev")
            content {
                // this repository *only* contains artifacts with group starting with "io.data2viz"
                // https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:declaring-repository-filter
                includeGroupByRegex("io\\.data2viz.*")
            }
        }
        maven {
            setUrl("https://maven.pkg.jetbrains.space/data2viz/p/maven/public")
            content {
                // this repository *only* contains artifacts with group starting with "io.data2viz"
                // https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:declaring-repository-filter
                includeGroupByRegex("io\\.data2viz.*")
            }
        }
    }
}
rootProject.name = "Fetal Movement"
include(":app")
