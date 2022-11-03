plugins {
    id("snapshot.android.feature")
    id("snapshot.android.library.compose")
}

android {
    namespace = "dev.ionice.snapshot.feature.entries"
}

dependencies {
    implementation(libs.accompanist.flowlayout)
    implementation(libs.accompanist.placeholderMaterial)

    implementation(libs.androidx.compose.material.iconsExtended)
}