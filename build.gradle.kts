// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.spotless)  apply false
    id ("org.jlleitschuh.gradle.ktlint") version "13.1.0" apply false


}

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}


