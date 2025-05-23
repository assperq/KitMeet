plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    //id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20"
}

kotlin {
    androidLibrary {
        namespace = "com.digital.supabaseclients"
        compileSdk = 35
        minSdk = 28

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    val xcfName = "supabaseClientsKit"

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

    val KTOR_VERSION = "3.0.0-rc-1"

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.auth.kt)
                implementation(libs.postgrest.kt)
                implementation(libs.storage.kt)
                implementation(libs.realtime.kt)
                implementation(libs.functions.kt)
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
                implementation("io.ktor:ktor-client-darwin:$KTOR_VERSION")
            }
        }
    }

}