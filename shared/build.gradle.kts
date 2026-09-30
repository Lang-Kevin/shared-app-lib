import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}

group   = "com.kevin.shared"
version = "0.3.0"

android {
    namespace   = "com.kevin.shared"
    compileSdk  = 35
    // All lib resources carry this prefix so they never collide with app resources.
    resourcePrefix = "shared_"
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

// Types from these libraries appear in the lib's public API (Modifier, Color, ImageVector,
// Typography, BackHandler, @Serializable, BaseRecordingService.serviceScope), so they are `api` and reach the apps transitively.
dependencies {
    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.coroutines.android)
    val composeBom = platform(libs.compose.bom)
    api(composeBom)
    api(libs.compose.ui)
    api(libs.compose.material3)
    api(libs.compose.material.icons.extended)
    api(libs.compose.ui.text.google.fonts)
    api(libs.activity.compose)
    testImplementation(libs.junit)
}
