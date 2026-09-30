plugins {
    alias(libs.plugins.socialfood.kmp.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
        }
        androidMain.dependencies {
            // androidx.core (toUri) comes in through activity-compose, as it did in :composeApp.
            implementation(libs.androidx.activity.compose)
        }
    }
}
