package pt.socialfood.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.library(alias: String): Provider<MinimalExternalModuleDependency> =
    findLibrary(alias).orElseThrow { IllegalArgumentException("Library '$alias' not found in libs.versions.toml") }

internal fun VersionCatalog.version(alias: String): String =
    findVersion(alias).orElseThrow { IllegalArgumentException("Version '$alias' not found in libs.versions.toml") }.requiredVersion

/** `:core:common` → `pt.socialfood.core.common`, `:feature:guide:impl` → `pt.socialfood.feature.guide.impl`. */
internal val Project.socialFoodNamespace: String
    get() = "pt.socialfood" + path.replace(':', '.').replace('-', '.')
