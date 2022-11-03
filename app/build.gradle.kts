plugins {
    id("snapshot.android.application")
    id("snapshot.android.application.compose")
    id("snapshot.android.hilt")
}

android {
    namespace = "dev.ionice.snapshot"

    defaultConfig {
        applicationId = "dev.ionice.snapshot"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        kapt {
            correctErrorTypes = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:notifications"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))

    implementation(project(":feature:entries"))
    implementation(project(":feature:favorites"))
    implementation(project(":feature:library"))
    implementation(project(":feature:search"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:tags"))

    implementation("androidx.core:core-ktx:1.9.0")

    // Compose
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)

    // Navigation for Compose
    implementation(libs.androidx.navigation.compose)

    // Accompanist for various UI functionality
    implementation(libs.accompanist.navigationAnimation)
    implementation(libs.accompanist.systemuicontroller)

    // Co-routines
    implementation(libs.kotlinx.coroutines.android)

    // WorkManager
    implementation(libs.androidx.work.ktx)

    // Hilt for DI
    implementation(libs.hilt.ext.work)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.ext.compiler)

    // Saved state for ViewModel
//    implementation "androidx.savedstate:savedstate-ktx:1.1.0"

    // Material 3 theming for launch screens and other misc. places
    implementation(libs.material)

    // Debugging
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)
}