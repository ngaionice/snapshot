plugins {
    id("snapshot.android.library")
    id("snapshot.android.library.compose")
}

android {
    namespace = "dev.ionice.snapshot.core.ui"
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.accompanist.placeholderMaterial)
    implementation(libs.accompanist.systemuicontroller)
}