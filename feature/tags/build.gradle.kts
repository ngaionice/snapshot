plugins {
    id("snapshot.android.feature")
    id("snapshot.android.library.compose")
}

android {
    namespace = "dev.ionice.snapshot.feature.tags"
}

dependencies {
    implementation(libs.accompanist.placeholderMaterial)
}