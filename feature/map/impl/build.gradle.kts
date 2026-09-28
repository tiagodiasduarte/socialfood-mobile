plugins {
    alias(libs.plugins.socialfood.kmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.maps)
            implementation(projects.feature.map.api)
            implementation(projects.feature.restaurant.api)
        }
    }
}
