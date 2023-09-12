plugins {
    id("snapshot.android.feature")
    id("snapshot.android.hilt")
    id("snapshot.android.library.compose")
}

android {
    namespace = "dev.ionice.snapshot.feature.library"
}

dependencies {
    implementation(libs.androidx.compose.foundation)

    implementation(libs.androidx.compose.material.iconsExtended)
}