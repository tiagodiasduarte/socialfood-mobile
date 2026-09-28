plugins {
    alias(libs.plugins.socialfood.kmp.library)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.domain)
            implementation(projects.core.database)
            implementation(projects.core.network)
            implementation(libs.kermit)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.androidx.paging.testing)
            implementation(libs.ktor.client.mock)
        }
    }
}
