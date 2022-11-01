plugins {
    id("snapshot.android.library")
}

android {
    namespace = "dev.ionice.snapshot.core.testing"
}

dependencies {
    api(libs.junit4)
    api(libs.kotlinx.coroutines.test)
    api(libs.truth)

    api(libs.androidx.test.core)
    api(libs.androidx.test.espresso.core)
    api(libs.androidx.test.rules)
    api(libs.androidx.compose.ui.test)
    api(libs.core.testing)
    api(libs.hilt.android.testing)
    api(libs.androidx.work.testing)
}