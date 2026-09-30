plugins {
    alias(libs.plugins.socialfood.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(libs.androidx.paging.common)
            api(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.androidx.paging.testing)
        }
    }
}
