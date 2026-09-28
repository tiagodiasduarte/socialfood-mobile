plugins {
    alias(libs.plugins.socialfood.kmp.library)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.navigation)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
