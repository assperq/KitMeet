plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.devtoolsKsp)
    alias(libs.plugins.kotlinx.serealization)
}

kotlin {
    androidLibrary {
        namespace = "com.digital.shared"
        compileSdk = 35
        minSdk = 28
    }

    val KTOR_VERSION = "3.0.0-rc-1"

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
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.navigation.compose)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.auth.kt)
                implementation(libs.postgrest.kt)
                implementation(libs.storage.kt)
                implementation(compose.material3)
                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)
                implementation(libs.kotlinx.serealization)
                implementation(project(":supabaseClients"))
                implementation(project(":profile"))
                implementation(libs.kamel.image)
                implementation("com.russhwolf:multiplatform-settings:1.1.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
                implementation("media.kamel:kamel-image-default:1.0.3")
                implementation("com.google.accompanist:accompanist-pager:0.28.0")
                implementation("com.google.accompanist:accompanist-pager-indicators:0.28.0")
                implementation("com.google.accompanist:accompanist-navigation-animation:0.28.0")
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$KTOR_VERSION")
                implementation("io.coil-kt:coil-compose:2.4.0")
            }
        }

        iosMain {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$KTOR_VERSION")
            }
        }
    }
}

