plugins {
    id("snapshot.android.library")
}

android {
    namespace = "dev.ionice.snapshot.core.sync"
}

dependencies {
    implementation(project(":core:database"))

    implementation(libs.androidx.activity.compose)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.work.ktx)

    implementation(libs.hilt.android)
    implementation(libs.hilt.ext.work)

    implementation(libs.android.gms.playServicesAuth)
    implementation(libs.google.api.client)
    implementation(libs.google.api.services.drive)
    implementation(libs.google.http.client.gson)
}