plugins {
    alias(libs.plugins.socialfood.kmp.library)
    alias(libs.plugins.socialfood.kmp.room)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.room.runtime)
            api(libs.androidx.paging.common)
            implementation(libs.androidx.room.paging)
        }
    }
}
