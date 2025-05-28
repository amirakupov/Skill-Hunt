// Top-level build file where can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("jvm") version "2.0.0" apply false
    id("io.ktor.plugin") version "3.1.2" apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
allprojects {
    group = "com.project"
    version = "0.0.1-SNAPSHOT"
}
