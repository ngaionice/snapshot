plugins {
    id("snapshot.android.feature")
    id("snapshot.android.library.compose")
}

android {
    namespace = "dev.ionice.snapshot.feature.search"
}

dependencies {
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.ui.tooling.preview)

    debugImplementation(libs.androidx.compose.ui.tooling)
}