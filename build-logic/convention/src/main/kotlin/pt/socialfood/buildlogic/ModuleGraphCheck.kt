package pt.socialfood.buildlogic

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency

private val composeCoreModules = setOf(":core:designsystem", ":core:ui", ":core:maps", ":core:navigation")
private val dataLayerModules = setOf(":core:data", ":core:network", ":core:database", ":core:datastore")
private val featureApiAllowed = setOf(":core:navigation", ":core:model", ":core:common")
private val featureApi = Regex(":feature:[^:]+:api")
private val featureImpl = Regex(":feature:[^:]+:impl")
private val standaloneFeatures = setOf(":feature:auth", ":feature:settings")

/**
 * Registers `checkModuleGraph`, which fails when this module's production dependencies break the
 * module rules documented in CLAUDE.md. Test configurations are ignored, so tests may use fakes
 * from any module through `:core:testing`.
 */
internal fun Project.configureModuleGraphCheck() {
    val modulePath = path
    val dependencyPaths = provider {
        configurations
            .filter { config ->
                val name = config.name
                !name.contains("test", ignoreCase = true) &&
                    (name.endsWith("Implementation") || name.endsWith("Api") || name.endsWith("CompileOnly"))
            }
            .flatMap { config -> config.dependencies.withType(ProjectDependency::class.java).map { it.path } }
            .filter { it != modulePath }
            .toSortedSet()
            .toList()
    }

    val checkModuleGraph = tasks.register("checkModuleGraph") {
        group = "verification"
        description = "Fails if this module depends on a module it isn't allowed to (see CLAUDE.md)."
        inputs.property("dependencyPaths", dependencyPaths)
        doLast {
            val violations = moduleGraphViolations(modulePath, dependencyPaths.get())
            if (violations.isNotEmpty()) {
                throw GradleException(
                    "Module dependency rules broken in $modulePath:\n" +
                        violations.joinToString("\n") { "  - $it" },
                )
            }
        }
    }
    tasks.named("check") { dependsOn(checkModuleGraph) }
}

internal fun moduleGraphViolations(module: String, dependencies: List<String>): List<String> =
    dependencies.mapNotNull { dependency -> violation(module, dependency) }

private fun violation(module: String, dependency: String): String? {
    val isFeature = module.startsWith(":feature:")
    val isFeatureImplLike = featureImpl.matches(module) || module in standaloneFeatures
    return when {
        featureApi.matches(module) && dependency !in featureApiAllowed ->
            "$dependency: feature api modules may only depend on ${featureApiAllowed.joinToString()}"
        isFeatureImplLike && (featureImpl.matches(dependency) || dependency in standaloneFeatures) ->
            "$dependency: features may only depend on other features' api modules, never their implementation"
        isFeature && dependency in dataLayerModules ->
            "$dependency: features use :core:domain interfaces; data-layer modules are bound in :composeApp"
        module.startsWith(":core:") && dependency.startsWith(":feature:") ->
            "$dependency: core modules must not depend on features"
        module.startsWith(":core:") && module !in composeCoreModules &&
            dependency in composeCoreModules ->
            "$dependency: non-UI core modules must not depend on Compose modules"
        else -> null
    }
}
