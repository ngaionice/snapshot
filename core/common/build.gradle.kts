plugins {
    id("snapshot.android.library")
    id("snapshot.android.hilt")
}

android {
    namespace = "dev.ionice.snapshot.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.android)
}