plugins {
    id("snapshot.android.library")
    id("snapshot.android.hilt")
}

android {
    namespace = "dev.ionice.snapshot.core.database"

    defaultConfig {
        kapt {
            correctErrorTypes = true
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:testing"))

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
}