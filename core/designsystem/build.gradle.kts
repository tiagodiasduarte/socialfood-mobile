plugins {
    alias(libs.plugins.socialfood.kmp.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            // androidx.core (toUri) comes in through activity-compose, as it did in :composeApp.
            implementation(libs.androidx.activity.compose)
        }
    }
}
