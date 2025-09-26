import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.dokka")
    id("com.diffplug.spotless")
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
    kotlin("kapt")
    id("jacoco")
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
            freeCompilerArgs.add("-Xopt-in=kotlin.RequiresOptIn")
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
        // Enable all lint checks
        checkAllWarnings = true

        // Treat warnings as errors (optional)
        warningsAsErrors = false

        // Abort build on errors
        abortOnError = false

        // Generate reports
        htmlReport = true
        xmlReport = true
        textReport = true

        // Report file locations
        htmlOutput = layout.buildDirectory.file("reports/lint/lint-results.html").get().asFile
        xmlOutput = layout.buildDirectory.file("reports/lint/lint-results.xml").get().asFile
        textOutput = layout.buildDirectory.file("reports/lint/lint-results.txt").get().asFile

        // Custom lint rules file (optional)
        lintConfig = file("../lint.xml")

        // Baseline file to ignore existing issues (optional)
        baseline = file("lint-baseline.xml")

        // Disable specific checks (examples)
        disable += listOf("ContentDescription", "InvalidPackage")

        // Enable specific checks
        enable += listOf("RtlHardcoded", "RtlCompat", "RtlEnabled")

        // Set severity levels
        warning += "MissingTranslation"
        error += "StopShip"
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude(
            "**/build/**",
            "**/Daos.kt",
            "**/Entities.kt",
            "**/QRDataBase.kt",
        )

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

// Ktlint Configuration
// val ktlint by configurations.creating

dependencies {

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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//
    implementation(libs.androidx.animation)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.androidx.foundation)

// for scan
    // CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Zxing
    implementation(libs.core.v333)

    // for generate
    implementation(libs.zxing.core)
    implementation(libs.zxing.android.embedded)

    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)

//    // Room
//    implementation("androidx.room:room-runtime:2.6.1")
//    implementation("androidx.room:room-compiler:2.6.1")
//    annotationProcessor("androidx.room:room-compiler:2.6.1")
//    implementation("androidx.room:room-ktx:2.6.1")
//    kapt( "androidx.room:room-compiler:2.6.1")

    // koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.androidx.compose)

    // life cycle
    implementation(libs.lifecycle.runtime.ktx.v282)
    // splash
    implementation(libs.androidx.core.splashscreen)
    testImplementation(kotlin("test"))

    implementation(libs.logcat)
    implementation(project(":designsystem"))

    detektPlugins(libs.detekt.formatting)

//    ktlint("com.pinterest:ktlint:0.50.0") {
//        attributes {
//            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
//        }
//    }

//    ktlint("com.pinterest.ktlint:ktlint-cli:<version>") {
//        attributes {
//            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
//        }
//    }
//    ktlint(libs.kotlin.stdlib.vkotlinversion)
//
//    ktlint(libs.ktlint.cli.v121)
//    implementation(libs.jetbrains.kotlin.stdlib)
}
// Apply JaCoCo plugin
apply(plugin = "jacoco")

// JaCoCo configuration
jacoco {
    toolVersion = "0.8.8"
}
kapt {
    correctErrorTypes = true
}
tasks.check {
    dependsOn("spotlessCheck")
}

// val codeQuality by tasks.registering {
//    group = "verification"
//    description = "Run all code quality checks"
//    dependsOn("spotlessCheck", "lintDebug")
// //    dependsOn("spotlessCheck")
// }

tasks.register("codeQuality") {
    dependsOn("ktlintCheck", "detekt", "dokkaGenerate")
    description = "Runs all code quality checks and generates documentation"
}

val codeFormat by tasks.registering {
    group = "formatting"
    description = "Format all code"
    dependsOn("spotlessApply")
}

val fullCheck by tasks.registering {
    group = "verification"
    description = "Run all checks including tests"
    dependsOn("clean", "codeQuality", "test")
}
// Custom JaCoCo task for combined coverage
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    group = "Reporting"
    description = "Generate Jacoco coverage reports for Debug build"

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter =
        listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            "android/**/*.*",
            "**/data/models/**", // Add your exclusions
            "**/di/**",
            "**/databinding/**",
            "**/android/databinding/**",
        )

    val debugTree = fileTree("$buildDir/tmp/kotlin-classes/debug")
    val mainSrc = "${project.projectDir}/src/main/java"
    val kotlinSrc = "${project.projectDir}/src/main/kotlin"

    sourceDirectories.setFrom(files(listOf(mainSrc, kotlinSrc)))
    classDirectories.setFrom(files(listOf(debugTree)))
    executionData.setFrom(
        fileTree(buildDir).include(
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
            "outputs/code_coverage/debugAndroidTest/connected/coverage.ec",
        ),
    )
//    finalizedBy("jacocoTestCoverageVerification")
}
tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("jacocoTestReport")

    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal() // 80% minimum coverage
            }
        }

        rule {
            element = "CLASS"
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}
