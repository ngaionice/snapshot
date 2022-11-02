plugins {
    id("snapshot.android.library")
    id("snapshot.android.hilt")
}

android {
    namespace = "dev.ionice.snapshot.testtools"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:sync"))

    api(libs.junit4)
    api(libs.kotlinx.coroutines.test)

    api(libs.androidx.test.core)
    api(libs.androidx.test.rules)
    api(libs.core.testing)
    api(libs.hilt.android.testing)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    kapt(libs.hilt.compiler)
}