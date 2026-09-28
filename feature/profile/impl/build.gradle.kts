plugins {
    alias(libs.plugins.socialfood.kmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.profile.api)
        }
    }
}
