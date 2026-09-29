plugins {
    alias(libs.plugins.socialfood.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            api(projects.core.domain)
            implementation(libs.kotlinx.serialization.json)
        }
        androidMain.dependencies {
            implementation(libs.androidx.datastore.preferences)
        }
    }
}
