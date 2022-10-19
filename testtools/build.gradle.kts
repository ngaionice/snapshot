plugins {
    id ("com.android.library")
    id ("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
}

val androidX_test_version = rootProject.extra.get("androidX_test_version") as String
val androidX_test_ext_kotlin_runner_version = rootProject.extra.get("androidX_test_ext_kotlin_runner_version") as String
val accompanist_version = rootProject.extra.get("accompanist_version") as String
val compose_version = rootProject.extra.get("compose_version") as String
val coroutines_version = rootProject.extra.get("coroutines_version") as String
val hamcrest_version = rootProject.extra.get("hamcrest_version") as String
val junit_version = rootProject.extra.get("junit_version") as String
val hilt_version = rootProject.extra.get("hilt_version") as String
val hilt_androidX_version = rootProject.extra.get("hilt_androidX_version") as String
val room_version = rootProject.extra.get("room_version") as String
val truth_version = rootProject.extra.get("truth_version") as String
val workmanager_version = rootProject.extra.get("workmanager_version") as String

android {
    namespace = "me.ionice.snapshot.testtools"
    compileSdk = 33

    defaultConfig {
        minSdk = 29
        targetSdk = 33
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("/META-INF/DEPENDENCIES")
        }
    }
}

dependencies {

    implementation (project(":app"))
    implementation ("androidx.core:core-ktx:1.9.0")

    implementation ("com.google.dagger:hilt-android:$hilt_version")
    implementation ("androidx.hilt:hilt-navigation-compose:$hilt_androidX_version")
    implementation ("com.google.dagger:hilt-android-testing:$hilt_version")
    kapt ("com.google.dagger:hilt-android-compiler:$hilt_version")

    implementation ("androidx.room:room-runtime:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    kapt ("androidx.room:room-compiler:$room_version")

    implementation ("androidx.test:core-ktx:$androidX_test_version")
    implementation ("androidx.test.ext:junit-ktx:$androidX_test_ext_kotlin_runner_version")
    implementation ("androidx.test:rules:$androidX_test_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")
}