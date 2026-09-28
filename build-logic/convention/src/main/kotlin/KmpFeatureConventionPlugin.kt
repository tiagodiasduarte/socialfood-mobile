import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import pt.socialfood.buildlogic.libs
import pt.socialfood.buildlogic.library

/** Feature `impl` modules: Compose + navigation, ViewModels and the core modules features may use. */
class KmpFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("socialfood.kmp.compose")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                implementation(project(":core:common"))
                implementation(project(":core:designsystem"))
                implementation(project(":core:domain"))
                implementation(project(":core:model"))
                implementation(project(":core:navigation"))
                implementation(project(":core:ui"))
                implementation(libs.library("androidx-lifecycle-runtimeCompose"))
                implementation(libs.library("androidx-lifecycle-viewmodelCompose"))
                implementation(libs.library("jetbrains-lifecycle-viewmodelNavigation3"))
                implementation(libs.library("jetbrains-navigation3-ui"))
                implementation(libs.library("koin-compose-viewmodel"))
                implementation(libs.library("koin-core"))
                implementation(libs.library("kotlinx-serialization-json"))
            }
        }
    }
}
