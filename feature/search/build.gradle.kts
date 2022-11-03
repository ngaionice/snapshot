plugins {
    id("snapshot.android.feature")
    id("snapshot.android.library.compose")
}

android {
    namespace = "dev.ionice.snapshot.feature.search"
}

dependencies {
    implementation(libs.androidx.compose.material)
}