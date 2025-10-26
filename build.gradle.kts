// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.spotless)  apply false
    id ("org.jlleitschuh.gradle.ktlint") version "13.1.0" apply false
    alias(libs.plugins.android.library) apply false
    id("com.osacky.doctor") version "0.12.1"
    jacoco
    id("org.jetbrains.dokka")
    id("io.gitlab.arturbosch.detekt") version "1.23.8"



}

apply(plugin = "com.osacky.doctor")

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("com.osacky.doctor:doctor-plugin:0.12.1")
    }
}

apply(plugin = "com.osacky.doctor")
//gradle.projectsEvaluated {
//    println("🔍 Checking if Gradle Doctor is applied...")
//    if (plugins.hasPlugin("com.osacky.doctor")) {
//        println("✅ Gradle Doctor plugin is successfully applied!")
//    } else {
//        println("❌ Gradle Doctor plugin is NOT applied!")
//    }
//}
doctor {

    // Warn when using jetifier
    warnWhenJetifierEnabled.set(true)

    // Negative Avoidance Threshold (in ms)
    negativeAvoidanceThreshold.set(500)


    // Disable specific checks
    disallowMultipleDaemons.set(false)

    // Java home check
    javaHome {
        ensureJavaHomeMatches.set(true)
        ensureJavaHomeIsSet.set(true)
    }
}


