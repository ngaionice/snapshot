buildscript {
    extra.apply {
        set("androidX_test_version", "1.4.0")
        set("androidX_test_ext_kotlin_runner_version", "1.1.4-beta01")
        set("accompanist_version", "0.25.1")
        set("compose_version", "1.2.1")
        set("coroutines_version", "1.6.3")
        set("hamcrest_version", "1.3")
        set("junit_version", "4.13.2")
        set("hilt_version", "2.42")
        set("hilt_androidX_version", "1.0.0")
        set("room_version", "2.4.3")
        set("truth_version", "1.1.3")
        set("workmanager_version", "2.7.1")
    }
    dependencies {
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.42")
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id ("com.android.application") version "7.3.1" apply false
    id ("com.android.library") version "7.3.1" apply false
    id ("org.jetbrains.kotlin.android") version "1.7.10" apply false
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}
