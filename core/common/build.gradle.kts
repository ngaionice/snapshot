plugins {
    id("snapshot.android.library")
}

android {
    namespace = "dev.ionice.snapshot.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}