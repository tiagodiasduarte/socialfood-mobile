plugins {
    alias(libs.plugins.socialfood.kmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coil.compose)
            implementation(projects.core.maps)
            implementation(projects.feature.author.api)
            implementation(projects.feature.guide.api)
            implementation(projects.feature.restaurant.api)
        }
    }
}
