import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "pt.socialfood.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.composeCompiler.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.kover.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = libs.plugins.socialfood.kmp.library.get().pluginId
            implementationClass = "KmpLibraryConventionPlugin"
        }
        register("kmpCompose") {
            id = libs.plugins.socialfood.kmp.compose.get().pluginId
            implementationClass = "KmpComposeConventionPlugin"
        }
        register("kmpFeature") {
            id = libs.plugins.socialfood.kmp.feature.get().pluginId
            implementationClass = "KmpFeatureConventionPlugin"
        }
        register("kmpRoom") {
            id = libs.plugins.socialfood.kmp.room.get().pluginId
            implementationClass = "KmpRoomConventionPlugin"
        }
    }
}
