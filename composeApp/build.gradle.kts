import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    cocoapods {
        version = "1.0"
        summary = "Shared KMM module"
        pod("FirebaseMessaging")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.cio)
            implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
            implementation(libs.firebase.messaging)
            implementation(project(":settings"))
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.navigation.compose)
            implementation(compose.material3)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.auth.kt)
            implementation(compose.materialIconsExtended)
            implementation(libs.postgrest.kt)
            implementation(libs.storage.kt)
            implementation(libs.kamel.image)
            implementation(libs.multiplatform.settings)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kamel.image.default)
            implementation(project(":registration"))
            implementation(project(":profile"))
            implementation(project(":cards"))
            implementation(project(":chat"))
            implementation(project(":supabaseClients"))
            implementation(project(":settings"))
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation("co.touchlab:stately-common:1.2.5")
            implementation("dev.gitlive:firebase-messaging:2.1.0")
        }
    }
}

android {
    namespace = "org.digital.kitmeet"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.digital.kitmeet"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.foundation.layout.android)
    debugImplementation(compose.uiTooling)
}

