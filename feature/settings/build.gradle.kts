plugins {
    id("snapshot.android.feature")
    id("snapshot.android.library.compose")
}

android {
    namespace = "dev.ionice.snapshot.feature.settings"
}

dependencies {
    implementation(project(":core:sync"))

    implementation(libs.accompanist.placeholderMaterial)
    implementation(libs.android.gms.playServicesAuth)

    implementation(libs.androidx.compose.material.iconsExtended)
    
    implementation(libs.androidx.compose.ui.tooling.preview)
}