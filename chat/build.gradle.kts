plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {

    androidLibrary {
        namespace = "com.digital.chat"
        compileSdk = 35
        minSdk = 24

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "chatKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.firebase.messaging)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.navigation.compose)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.auth.kt)
                implementation(libs.postgrest.kt)
                implementation(libs.storage.kt)
                implementation(libs.realtime.kt)
                implementation(libs.functions.kt)
                implementation(compose.materialIconsExtended)
                implementation(libs.kotlinx.datetime)
                implementation(libs.uuid)
                implementation(libs.kamel.image.default)
                implementation(libs.accompanist.pager)
                implementation(libs.accompanist.pager.indicators)
                implementation(libs.accompanist.navigation.animation)
                implementation(project(":cards"))
                implementation(project(":supabaseClients"))
                implementation(project(":profile"))
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.firebase.messaging)
                implementation(libs.ktor.client.cio)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.test.junit)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

