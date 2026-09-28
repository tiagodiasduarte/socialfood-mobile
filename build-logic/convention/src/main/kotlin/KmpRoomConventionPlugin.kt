import androidx.room.gradle.RoomExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import pt.socialfood.buildlogic.libs
import pt.socialfood.buildlogic.library

/** Room with KSP for every target, schemas exported to `<module>/schemas`. */
class KmpRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.google.devtools.ksp")
            apply("androidx.room")
        }

        extensions.configure<RoomExtension> {
            schemaDirectory("$projectDir/schemas")
        }

        extensions.configure<KotlinMultiplatformExtension> {
            sourceSets.commonMain.dependencies {
                implementation(libs.library("androidx-room-runtime"))
                implementation(libs.library("androidx-sqlite-bundled"))
            }
        }

        // KSP creates its per-target configurations lazily, so attach the compiler as they appear.
        val kspConfigurations = setOf("kspAndroid", "kspIosArm64", "kspIosSimulatorArm64")
        configurations.matching { it.name in kspConfigurations }.configureEach {
            dependencies.addLater(libs.library("androidx-room-compiler"))
        }
    }
}
