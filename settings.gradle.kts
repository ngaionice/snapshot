pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "snapshot"
enableFeaturePreview("VERSION_CATALOGS")
include(":app")
include(":testtools")

include(":core:common")
include(":core:data")
include(":core:database")
include(":core:model")
include(":core:sync")
include(":core:testing")
include(":core:ui")

include(":feature:entries")
include(":feature:library")
include(":feature:settings")
