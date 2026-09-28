plugins {
    alias(libs.plugins.socialfood.kmp.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)
        }
    }
}
