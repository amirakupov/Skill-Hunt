// PROJECT-LEVEL build.gradle.kts
plugins {

    alias(libs.plugins.ksp) apply false // <<< KSP DECLARATION #2 (Conflicting Alias)
    //alias(libs.plugins.google.devtools.ksp) apply false // <<< KSP DECLARATION #1
    // Existing plugins
    kotlin("jvm") version "2.0.0" apply false
    id("io.ktor.plugin") version "3.1.2" apply false // Assuming still use Ktor
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false

    // Hilt plugin definition
    alias(libs.plugins.hilt.android) apply false
   // alias(libs.plugins.kotlin.ksp) apply false // <<< KSP DECLARATION #2 (Conflicting Alias)

    // If were to use KSP (optional, alternative to kapt)
    // alias(libs.plugins.kotlin.ksp) apply false // <<< KSP DECLARATION #3 (Commented out, but shows confusion)
}

allprojects {
    group = "com.project"
    version = "0.0.1-SNAPSHOT"
}