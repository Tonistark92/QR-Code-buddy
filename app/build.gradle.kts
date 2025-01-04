plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.iscoding.qrcode"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.iscoding.qrcode"
        minSdk = 24
        targetSdk = 34
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
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
}

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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Activity Compose
//    implementation ("androidx.activity:activity-compose:1.9.0")
// for scan
    // CameraX
    implementation ("androidx.camera:camera-camera2:1.3.3")
    implementation ("androidx.camera:camera-lifecycle:1.3.3")
    implementation ("androidx.camera:camera-view:1.3.3")

    // Zxing
    implementation ("com.google.zxing:core:3.3.3")

    //for generate
    implementation ("com.google.zxing:core:3.3.3")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")


    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")
    implementation("io.coil-kt:coil-compose:1.4.0")

//    // Room
//    implementation("androidx.room:room-runtime:2.6.1")
//    implementation("androidx.room:room-compiler:2.6.1")
//    annotationProcessor("androidx.room:room-compiler:2.6.1")
//    implementation("androidx.room:room-ktx:2.6.1")
//    kapt( "androidx.room:room-compiler:2.6.1")

    // koin
    implementation ("io.insert-koin:koin-android:3.5.3")
    implementation ("io.insert-koin:koin-androidx-navigation:3.2.0-beta-1")
    implementation ("io.insert-koin:koin-androidx-compose:3.5.3")

    //life cycle
//    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
}
kapt {
    correctErrorTypes = true
}