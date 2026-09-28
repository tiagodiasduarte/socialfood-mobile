package pt.socialfood.buildlogic

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.io.File

/**
 * ktlint + detekt with the shared root config. Each module keeps its own baselines
 * (`ktlint-baseline.xml`, `config/detekt/baseline.xml`) when it has pre-existing violations.
 */
internal fun Project.configureQuality() {
    with(pluginManager) {
        apply("org.jlleitschuh.gradle.ktlint")
        apply("io.gitlab.arturbosch.detekt")
    }

    extensions.configure<KtlintExtension> {
        ignoreFailures.set(false)
        val ktlintBaseline = file("ktlint-baseline.xml")
        if (ktlintBaseline.exists()) baseline.set(ktlintBaseline)
        filter {
            exclude("**/build/**")
            exclude("**/generated/**")
            exclude { element -> element.file.path.contains("${File.separatorChar}build${File.separatorChar}") }
        }
        reporters {
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.HTML)
        }
    }

    extensions.configure<DetektExtension> {
        buildUponDefaultConfig = true
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
        // Missing baseline files are ignored by detekt; `detektBaseline` writes this path.
        baseline = file("config/detekt/baseline.xml")
        source.setFrom(
            "src/commonMain/kotlin",
            "src/androidMain/kotlin",
            "src/iosMain/kotlin",
            "src/commonTest/kotlin",
        )
    }

    tasks.withType<Detekt>().configureEach {
        reports {
            html.required.set(true)
            sarif.required.set(true)
        }
    }
}
