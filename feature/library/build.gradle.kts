plugins {
    id("snapshot.android.feature")
    id("snapshot.android.hilt")
    id("snapshot.android.library.compose")
}

android {
    namespace = "dev.ionice.snapshot.feature.library"
}

dependencies {
    implementation(libs.accompanist.pager)

    implementation(libs.androidx.compose.material.iconsExtended)
}