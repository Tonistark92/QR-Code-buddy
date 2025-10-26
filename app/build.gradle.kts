import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("org.jlleitschuh.gradle.ktlint")
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka")
    kotlin("kapt")
    id("jacoco")
}
val sentryDsn: String? = project.findProperty("SENTRY_DSN") as String?

android {
    namespace = "com.iscoding.qrcode"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.iscoding.qrcode"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "SENTRY_DSN", "\"${sentryDsn ?: ""}\"")
        manifestPlaceholders["sentryDsn"] = project.findProperty("SENTRY_DSN") ?: ""
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        checkAllWarnings = true
        warningsAsErrors = false
        abortOnError = false
        htmlReport = true
        xmlReport = true
        textReport = true

        htmlOutput = layout.buildDirectory.file("reports/lint/lint-results.html").get().asFile
        xmlOutput = layout.buildDirectory.file("reports/lint/lint-results.xml").get().asFile
        textOutput = layout.buildDirectory.file("reports/lint/lint-results.txt").get().asFile

        lintConfig = file("../lint.xml")
        baseline = file("lint-baseline.xml")

        disable += listOf("ContentDescription", "InvalidPackage")
        enable += listOf("RtlHardcoded", "RtlCompat", "RtlEnabled")

        warning += "MissingTranslation"
        error += "StopShip"
    }
}

detekt {
    toolVersion = "1.23.4"
    config.setFrom(file("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
}

ktlint {
    version.set("1.0.1")
    debug.set(true)
    verbose.set(true)
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**", "**/Daos.kt", "**/Entities.kt", "**/QRDataBase.kt")
        ktlint("1.2.1")
            .editorConfigOverride(
                mapOf(
                    "ktlint_disabled_rules" to "function-naming",
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                    "indent_size" to "4",
                ),
            )
            .setEditorConfigPath("$rootDir/.editorconfig")
        leadingTabsToSpaces()
        trimTrailingWhitespace()
        endWithNewline()
    }

    java {
        target("**/*.java")
        googleJavaFormat("1.22.0")
    }

    format("xml") {
        target("**/*.xml")
        trimTrailingWhitespace()
    }

    kotlinGradle {
        target("*.kts")
        ktlint("1.2.1")
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    // Core & AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.animation)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)
    implementation(libs.androidx.core.splashscreen)

    // CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // QR libraries
    implementation(libs.core.v333)
    implementation(libs.zxing.core)
    implementation(libs.zxing.android.embedded)

    // Koin DI
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.androidx.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Logging
    implementation(libs.logcat)

    // Project modules
    implementation(project(":designsystem"))
//    lintChecks(project(":lint-rules"))
    implementation(libs.arrow.core)

    implementation(libs.sentry.android)

    // Detekt formatting plugin
    detektPlugins(libs.detekt.formatting)
}

// JaCoCo
// apply(plugin = "jacoco")
//
// jacoco {
//    toolVersion = "0.8.8"
// }

// Tasks
tasks.check {
    dependsOn("spotlessCheck")
}

tasks.register("codeQuality") {
//    dependsOn("ktlintCheck", "detekt", "dokkaGenerate")
    dependsOn("spotlessCheck", "lintDebug")
    description = "Runs all code quality checks and generates documentation"
    group = "verification"
}

val codeFormat by tasks.registering {
    dependsOn("spotlessApply")
    description = "Format all code"
    group = "formatting"
}

val fullCheck by tasks.registering {
    dependsOn("clean", "codeQuality", "test")
    description = "Run all checks including tests"
    group = "verification"
}

// Jacoco report
// tasks.register<JacocoReport>("jacocoTestReport") {
//    dependsOn("testDebugUnitTest")
//    group = "Reporting"
//    description = "Generate Jacoco coverage reports for Debug build"
//
//    reports {
//        xml.required.set(true)
//        html.required.set(true)
//        csv.required.set(false)
//    }
//
//    val fileFilter =
//        listOf(
//            "**/R.class",
//            "**/R$*.class",
//            "**/BuildConfig.*",
//            "**/Manifest*.*",
//            "**/*Test*.*",
//            "android/**/*.*",
//            "**/data/models/**",
//            "**/di/**",
//            "**/databinding/**",
//            "**/android/databinding/**",
//        )
//
//    val debugTree = fileTree("$buildDir/tmp/kotlin-classes/debug")
//    val mainSrc = "${project.projectDir}/src/main/java"
//    val kotlinSrc = "${project.projectDir}/src/main/kotlin"
//
//    sourceDirectories.setFrom(files(listOf(mainSrc, kotlinSrc)))
//    classDirectories.setFrom(files(listOf(debugTree)))
//    executionData.setFrom(
//        fileTree(buildDir).include(
//            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
//            "outputs/code_coverage/debugAndroidTest/connected/coverage.ec",
//        ),
//    )
// }
//
// tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
//    dependsOn("jacocoTestReport")
//    violationRules {
//        rule {
//            limit { minimum = "0.80".toBigDecimal() } // 80% coverage
//        }
//        rule {
//            element = "CLASS"
//            limit {
//                counter = "BRANCH"
//                value = "COVEREDRATIO"
//                minimum = "0.70".toBigDecimal()
//            }
//        }
//    }
// }
