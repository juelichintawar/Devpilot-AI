import java.security.KeyStore
import java.security.MessageDigest

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.example.devpilotai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.devpilotai"
        minSdk = 26
        targetSdk = 35

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

configurations.configureEach {
    exclude(
        group = "org.jetbrains",
        module = "annotations-java5"
    )
}

dependencies {

    // --------------------------------------------------
    // AndroidX
    // --------------------------------------------------
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // --------------------------------------------------
    // Navigation
    // --------------------------------------------------
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // --------------------------------------------------
    // Room Database
    // --------------------------------------------------
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // --------------------------------------------------
    // Retrofit / Networking
    // --------------------------------------------------
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // --------------------------------------------------
    // Firebase
    // --------------------------------------------------
    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    implementation(libs.play.services.auth)

    // --------------------------------------------------
    // Firebase AI Logic
    // --------------------------------------------------
    implementation("com.google.firebase:firebase-ai")

    // Firebase App Check - Debug
    implementation("com.google.firebase:firebase-appcheck-debug")

    // Required for Java Firebase AI Logic
    implementation("com.google.guava:guava:31.0.1-android")
    implementation("org.reactivestreams:reactive-streams:1.0.4")

    // --------------------------------------------------
    // Glide
    // --------------------------------------------------
    implementation(libs.glide)
    annotationProcessor(libs.glide)

    // --------------------------------------------------
    // Lottie Animations
    // --------------------------------------------------
    implementation(libs.lottie)

    // --------------------------------------------------
    // Google ML Kit
    // --------------------------------------------------
    implementation(libs.mlkit.face)
    implementation(libs.mlkit.text)

    // --------------------------------------------------
    // Lifecycle
    // --------------------------------------------------
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // --------------------------------------------------
    // Markwon - Markdown Rendering
    // --------------------------------------------------
    implementation(libs.markwon.core)
    implementation(libs.markwon.html)

    // --------------------------------------------------
    // Security
    // --------------------------------------------------
    implementation(libs.security.crypto)

    // --------------------------------------------------
    // JetBrains Annotations
    // --------------------------------------------------
    implementation(libs.jetbrains.annotations)

    // --------------------------------------------------
    // Testing
    // --------------------------------------------------
    testImplementation(libs.junit)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}