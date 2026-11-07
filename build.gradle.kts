// Top-level build file. Plugin versions live in gradle/libs.versions.toml.
//
// AGP 9 has built-in Kotlin: the org.jetbrains.kotlin.android plugin must NOT be applied.
// Per Google's AGP guidance, a newer Kotlin Gradle plugin than the one bundled with AGP is
// put on the buildscript classpath. KEEP THIS VERSION EQUAL to `kotlin` in libs.versions.toml
// (the Compose compiler plugin must match the Kotlin version).
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.10")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
}
