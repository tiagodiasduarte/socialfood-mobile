plugins {
    alias(libs.plugins.socialfood.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.database)
            api(projects.core.domain)
            api(projects.core.network)
            api(libs.kotlin.test)
            api(libs.kotlinx.coroutines.test)
        }
    }
}
