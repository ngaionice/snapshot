plugins {
    id("snapshot.android.library")
    id("snapshot.android.hilt")
}

android {
    namespace = "dev.ionice.snapshot.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:model"))
    implementation(project(":core:notifications"))
    implementation(project(":core:sync"))

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.work.ktx)

    implementation(libs.hilt.android)
    implementation(libs.hilt.ext.work)
    kapt(libs.hilt.compiler)

    implementation(libs.android.gms.playServicesAuth)
    implementation(libs.google.api.client)
}