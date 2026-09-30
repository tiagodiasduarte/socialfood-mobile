rootProject.name = "SocialFood"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":composeApp")
include(":core:common")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:designsystem")
include(":core:domain")
include(":core:maps")
include(":core:navigation")
include(":core:network")
include(":core:testing")
include(":core:ui")
include(":feature:auth")
include(":feature:author:api")
include(":feature:author:impl")
include(":feature:favourite:api")
include(":feature:favourite:impl")
include(":feature:guide:api")
include(":feature:guide:impl")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:map:api")
include(":feature:map:impl")
include(":feature:profile:api")
include(":feature:profile:impl")
include(":feature:restaurant:api")
include(":feature:restaurant:impl")
include(":feature:search:api")
include(":feature:search:impl")
include(":feature:settings")
