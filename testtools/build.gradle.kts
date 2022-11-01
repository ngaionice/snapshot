plugins {
    id("snapshot.android.library")
    id("snapshot.android.hilt")
}

android {
    namespace = "dev.ionice.snapshot.testtools"

    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("/META-INF/DEPENDENCIES")
        }
    }
}

dependencies {
    implementation(project(":app"))
    implementation(project(":core:database"))

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