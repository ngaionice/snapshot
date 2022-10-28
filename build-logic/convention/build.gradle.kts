plugins {
    `kotlin-dsl`
}

group = "me.ionice.snapshot.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplicationCompose") {
            id = "snapshot.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplication") {
            id = "snapshot.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "snapshot.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "snapshot.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "snapshot.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidTest") {
            id = "snapshot.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("androidHilt") {
            id = "snapshot.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
    }
}
