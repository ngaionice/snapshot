plugins {
    id("snapshot.android.feature")
    id("snapshot.android.hilt")
    id("snapshot.android.library.compose")
}

android {
    namespace = "dev.ionice.snapshot.feature.favorites"
}

dependencies {
    implementation(libs.androidx.activity.compose)

    implementation(libs.accompanist.navigationAnimation)
    implementation(libs.androidx.compose.material3)

    androidTestImplementation(project(":testtools"))
}