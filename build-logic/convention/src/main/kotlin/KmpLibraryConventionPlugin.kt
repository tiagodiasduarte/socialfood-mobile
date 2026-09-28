import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import pt.socialfood.buildlogic.configureQuality
import pt.socialfood.buildlogic.libs
import pt.socialfood.buildlogic.library
import pt.socialfood.buildlogic.socialFoodNamespace
import pt.socialfood.buildlogic.version

/**
 * Base for every shared module: Android (KMP library plugin) + iosArm64 + iosSimulatorArm64,
 * JVM 21, ktlint/detekt with the shared config, Kover, and the common test dependencies.
 */
class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("com.android.kotlin.multiplatform.library")
            apply("org.jetbrains.kotlinx.kover")
        }
        configureQuality()

        // Same variant name as :composeApp's business-logic variant so the aggregated report merges them.
        extensions.configure<KoverProjectExtension> {
            currentProject {
                copyVariant("businessLogic", "android")
            }
        }

        extensions.configure<KotlinMultiplatformExtension> {
            (this as org.gradle.api.plugins.ExtensionAware).extensions
                .configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
                    namespace = socialFoodNamespace
                    compileSdk = libs.version("android-compileSdk").toInt()
                    minSdk = libs.version("android-minSdk").toInt()
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_21)
                    }
                    withHostTest {}
                }

            iosArm64()
            iosSimulatorArm64()

            sourceSets.commonTest.dependencies {
                implementation(libs.library("kotlin-test"))
                implementation(libs.library("kotlinx-coroutines-test"))
                implementation(libs.library("turbine"))
                if (path != ":core:testing") rootProject.findProject(":core:testing")?.let { implementation(it) }
            }
        }
    }
}
