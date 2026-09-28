import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import pt.socialfood.buildlogic.libs
import pt.socialfood.buildlogic.library
import pt.socialfood.buildlogic.socialFoodNamespace

/** [KmpLibraryConventionPlugin] + Compose Multiplatform, with a per-module public `Res` class. */
class KmpComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("socialfood.kmp.library")
            apply("org.jetbrains.kotlin.plugin.compose")
            apply("org.jetbrains.compose")
        }

        extensions.configure<KotlinMultiplatformExtension> {
            (this as ExtensionAware).extensions.configure<KotlinMultiplatformAndroidLibraryTarget>("android") {
                androidResources.enable = true
            }
            sourceSets.commonMain.dependencies {
                implementation(libs.library("compose-components-resources"))
                implementation(libs.library("compose-foundation"))
                implementation(libs.library("compose-material3"))
                implementation(libs.library("compose-material-icons-extended"))
                implementation(libs.library("compose-runtime"))
                implementation(libs.library("compose-ui"))
                implementation(libs.library("compose-ui-tooling-preview"))
            }
        }

        extensions.configure<ComposeExtension> {
            (this as ExtensionAware).extensions.configure<ResourcesExtension> {
                publicResClass = true
                packageOfResClass = "$socialFoodNamespace.generated.resources"
            }
        }
    }
}
