plugins {
    alias(libs.plugins.socialfood.kmp.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.feature.guide.api)
            implementation(projects.feature.home.api)
            implementation(projects.feature.restaurant.api)
            implementation(projects.feature.search.api)
        }
    }
}
