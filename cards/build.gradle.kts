plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
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
                // Add KMP dependencies here
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

        iosMain {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$KTOR_VERSION")
            }
        }
    }

}