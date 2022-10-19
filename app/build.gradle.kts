plugins {
    id ("com.android.application")
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
    compileSdk = 33

    defaultConfig {
        applicationId = "me.ionice.snapshot"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        kapt {
            correctErrorTypes = true
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.1"
    }

    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
            excludes.add("/META-INF/DEPENDENCIES")
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")

    // Compose
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.compose.material3:material3:1.0.0-rc01")
    implementation("androidx.compose.material:material:$compose_version")
    implementation("androidx.compose.material:material-icons-extended:$compose_version")

    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.5.2")

    // Accompanist for various UI functionality
    implementation ("com.google.accompanist:accompanist-systemuicontroller:$accompanist_version")
    implementation ("com.google.accompanist:accompanist-placeholder-material:$accompanist_version")
    implementation ("com.google.accompanist:accompanist-navigation-animation:$accompanist_version")
    implementation ("com.google.accompanist:accompanist-flowlayout:$accompanist_version")
    implementation ("com.google.accompanist:accompanist-pager:$accompanist_version")

    // Lifecycles
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")

    // Co-routines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")

    // Room
    implementation ("androidx.room:room-runtime:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    kapt ("androidx.room:room-compiler:$room_version")

    // DataStore
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    // WorkManager
    implementation ("androidx.work:work-runtime-ktx:$workmanager_version")

    // Hilt for DI
    implementation ("com.google.dagger:hilt-android:$hilt_version")
    implementation ("androidx.hilt:hilt-work:1.0.0")
    implementation ("androidx.hilt:hilt-navigation-compose:$hilt_androidX_version")
    kapt ("com.google.dagger:hilt-android-compiler:$hilt_version")
    kapt ("androidx.hilt:hilt-compiler:1.0.0")

    // Saved state for ViewModel
//    implementation "androidx.savedstate:savedstate-ktx:1.1.0"

    // Material 3 theming for launch screens and other misc. places
    implementation ("com.google.android.material:material:1.6.1")

    // Google Services: for access to Google Drive
    implementation ("com.google.android.gms:play-services-auth:20.3.0")
    implementation ("com.google.http-client:google-http-client-gson:1.41.8")
    implementation ("com.google.api-client:google-api-client-android:1.34.1")
    implementation ("com.google.apis:google-api-services-drive:v3-rev20220508-1.32.1")

    // Testing
    implementation ("androidx.test:core:$androidX_test_version")
    testImplementation (project(":testtools"))
    testImplementation ("androidx.test:core-ktx:$androidX_test_version")
    testImplementation ("androidx.test.ext:junit-ktx:$androidX_test_ext_kotlin_runner_version")
    testImplementation ("androidx.test:rules:$androidX_test_version")
    testImplementation ("com.google.dagger:hilt-android-testing:$hilt_version")
    testImplementation ("com.google.truth:truth:$truth_version")
    testImplementation ("junit:junit:$junit_version")
    testImplementation ("org.hamcrest:hamcrest-all:$hamcrest_version")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")
    kaptTest ("com.google.dagger:hilt-compiler:$hilt_version")

    // Android testing
    androidTestImplementation (project(":testtools"))
    androidTestImplementation ("androidx.test.ext:junit:1.1.3")
    androidTestImplementation ("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:$compose_version")
    androidTestImplementation ("androidx.work:work-testing:$workmanager_version")
    androidTestImplementation ("com.google.dagger:hilt-android-testing:$hilt_version")
    androidTestImplementation ("com.google.truth:truth:$truth_version")
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version")
    kaptAndroidTest ("com.google.dagger:hilt-compiler:$hilt_version")

    // Debugging
    debugImplementation ("androidx.compose.ui:ui-tooling:$compose_version")
    debugImplementation ("androidx.compose.ui:ui-test-manifest:$compose_version")
}