// No org.jetbrains.kotlin.android plugin here: AGP 9 provides built-in Kotlin.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.youplayertube.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.youplayertube.app"
        minSdk = 26
        targetSdk = 37 // provisional; Android 17 behaviour changes not yet reviewed
        versionCode = 1
        versionName = "0.0.2-phase02"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
}
