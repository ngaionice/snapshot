plugins {
    id("snapshot.android.library")
}

android {
    namespace = "dev.ionice.snapshot.core.notifications"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}