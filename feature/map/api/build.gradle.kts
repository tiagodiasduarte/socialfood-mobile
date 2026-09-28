plugins {
    alias(libs.plugins.socialfood.kmp.library)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.navigation)
            api(projects.core.common)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
