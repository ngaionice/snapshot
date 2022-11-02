plugins {
    id("snapshot.android.feature")
    id("snapshot.android.hilt")
    id("snapshot.android.library.compose")
}

android {
    namespace = "dev.ionice.snapshot.feature.library"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
}