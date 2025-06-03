plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    //alias(libs.plugins.kotlin.ksp)// For Kotlinx Serialization

    // For Hilt:
   // LAST CHANGE kotlin("kapt") // Apply kapt for annotation processing
    // OR if chose KSP (Kotlin Symbol Processing) for Hilt:
    // alias(libs.plugins.kotlin.ksp)
    //alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.hilt.android) // Apply the Hilt plugin
}

android {
    namespace = "com.project.skill_hunt" // Or actual namespace
    compileSdk = 35 // Or target SDK

    defaultConfig {
        applicationId = "com.project.skill_hunt" // Or actual application ID
        minSdk = 24 // Or min SDK
        targetSdk = 35 // Or target SDK
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15" // Ensure this matches Compose & Kotlin version compatibility
        // For Kotlin 2.0.0, might need a newer Compose Compiler.
        // Check https://developer.android.com/jetpack/androidx/releases/compose-compiler
        // e.g., for Kotlin 2.0.0, it's typically "1.5.10" or similar for stable Compose.
        // "1.5.15" might be too new or from a BOM that implies it.
        // If "1.5.15" comes from composeBom = "2024.04.01", it *should* be compatible.
    }
}

dependencies {
    // existing dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom)) // This controls many compose versions
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Ktor (ensure ktor version is managed in libs.versions.toml for consistency if desired)
    val ktorVersion = "3.1.2" // Or better: libs.versions.ktor.get() if defined in TOML
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Kotlinx Coroutines & Serialization (ensure versions are consistent, prefer from TOML)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0") // Or chosen version
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // Or chosen version

    // Retrofit & OkHttp (ensure versions are managed in TOML for consistency)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson or Kotlinx.Serialization converter
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0") // If using Retrofit with Kotlinx Serialization

    // Other UI/Navigation dependencies
    // implementation("androidx.activity:activity-compose:1.9.0") // Covered by libs.androidx.activity.compose
    // implementation("androidx.compose.material3:material3:1.2.1") // Covered by libs.androidx.material3 (and Compose BOM)
    implementation("androidx.navigation:navigation-compose:2.7.7") // Or manage version in TOML

    // --- Hilt Dependencies ---
    implementation(libs.hilt.android)
   // kapt(libs.hilt.compiler) // For Annotation Processing with kapt
    // OR if using KSP:
    // ksp(libs.hilt.compiler)
    // kapt(libs.hilt.compiler) // REMOVE THIS
    ksp(libs.hilt.compiler)
    // --- Javax Inject (for @Inject annotation) ---
    implementation(libs.javax.inject) // This provides the @Inject annotation
}

// Optional: If choose KSP over kapt for Hilt (and other annotation processors)
// ksp {
//    arg("dagger.fastInit", "enabled") // Recommended for faster Hilt initialization
//    arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true") // Can be useful
//}