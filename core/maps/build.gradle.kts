plugins {
    alias(libs.plugins.socialfood.kmp.compose)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.designsystem)
            implementation(projects.core.domain)
        }
        androidMain.dependencies {
            implementation(libs.google.maps.compose)
            implementation(libs.play.services.maps)
        }
    }
}
