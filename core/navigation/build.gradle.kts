plugins {
    id("snapshot.android.library")
}

android {
    namespace = "dev.ionice.snapshot.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.compose)
}