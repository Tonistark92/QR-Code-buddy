pluginManagement {
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
    plugins {
//        id("com.osacky.doctor") version "0.12.1"
        id("org.jetbrains.dokka") version "2.0.0"
        id("de.fayard.refreshVersions") version "0.60.5"
    }

}
//
//plugins {
//    id("de.fayard.refreshVersions") version "0.60.5"
//////                            # available:"0.60.6"
//
//
//}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "QRCode Buddy"
include(":app")
include(":designsystem")
include(":lint-rules")
