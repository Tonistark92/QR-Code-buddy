plugins {
    id("java-library")
    id("kotlin")
    id("org.jetbrains.kotlin.jvm")

}
dependencies {
    compileOnly(libs.lint.api)     // for Lint API classes
    compileOnly(libs.lint.checks) // for reference to built-in checks
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

