plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.diffplug.spotless")
    kotlin("kapt")
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
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
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
        htmlOutput = file("$buildDir/reports/lint/lint-results.html")
        xmlOutput = file("$buildDir/reports/lint/lint-results.xml")
        textOutput = file("$buildDir/reports/lint/lint-results.txt")

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
            "**/Daos.kt", // Exclude specific empty file
            "**/Entities.kt", // Exclude specific empty file
            "**/QRDataBase.kt", // Exclude specific empty file
            "**/QRCodeRepositoryImp.kt", // Exclude specific empty file
            "**/AllStorageImagesUiState.kt", // Exclude specific empty file
            "**/QrDetailEvent.kt", // Exclude specific empty file
            "**/QrDetailUiState.kt", // Exclude specific empty file
            "**/QrDetailViewModel.kt", // Exclude specific empty file
            // Add more files as needed:
            // "**/AnotherEmptyFile.kt"
        )

        ktlint("1.2.1")
            .editorConfigOverride(
                mapOf(
                    "ktlint_disabled_rules" to "function-naming",
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
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
        indentWithSpaces()
    }
    kotlinGradle {
        target("*.kts")
        ktlint("1.2.1")
    }
}

// Ktlint Configuration
val ktlint by configurations.creating

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
kapt {
    correctErrorTypes = true
}
tasks.check {
    dependsOn("spotlessCheck")
}

val codeQuality by tasks.registering {
    group = "verification"
    description = "Run all code quality checks"
//    dependsOn("spotlessCheck", "lintDebug")
    dependsOn("spotlessCheck")
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

// val ktlintCheck by tasks.registering(JavaExec::class) {
//    group = "verification"
//    description = "Check Kotlin code style."
//    classpath = ktlint
//    mainClass.set("com.pinterest.ktlint.Main")
//    args("src/**/*.kt", "**.kts", "!**/build/**")
// }
//
// val ktlintFormat by tasks.registering(JavaExec::class) {
//    group = "formatting"
//    description = "Fix Kotlin code style deviations."
//    classpath = ktlint
//    mainClass.set("com.pinterest.ktlint.Main")
//    jvmArgs = listOf("--add-opens=java.base/java.lang=ALL-UNNAMED") // Might be needed for newer Java versions
//    args("-F", "src/**/*.kt", "**.kts", "!**/build/**")
// }
//
// // Make ktlintCheck run with check task
// tasks.check {
//    dependsOn(ktlintCheck)
// }
//
// // Convenience tasks
// val codeQuality by tasks.registering {
//    group = "verification"
//    description = "Run all code quality checks"
//    dependsOn("spotlessCheck", "ktlintCheck", "lintDebug")
// }
//
// val codeFormat by tasks.registering {
//    group = "formatting"
//    description = "Format all code"
//    dependsOn("spotlessApply", "ktlintFormat")
// }
//
// val fullCheck by tasks.registering {
//    group = "verification"
//    description = "Run all checks including tests"
//    dependsOn("clean", "codeQuality", "test")
// }
