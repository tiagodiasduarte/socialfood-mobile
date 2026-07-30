import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val appId = "pt.socialfood"
val appNamespace = "pt.socialfood"
val appVersionName = "0.1.0"
val buildDateKey = "BUILD_DATE"
val githubActionsKey = "GITHUB_ACTIONS"
val githubRunNumberKey = "GITHUB_RUN_NUMBER"
val googleClientIdKey = "GOOGLE_CLIENT_ID"
val googleClientIdWebKey = "GOOGLE_CLIENT_ID_WEB"
val javaVersion = JavaVersion.VERSION_21
val keyAliasKey = "KEY_ALIAS"
val keyPasswordKey = "KEY_PASSWORD"
val releaseKeystoreFileName = "socialfood-release-key.jks"
val storePasswordKey = "STORE_PASSWORD"

val isGithubActions = System.getenv(githubActionsKey) == "true"

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}

fun configValue(key: String, localDefault: String? = null): String {
    val value = System.getenv(key) ?: localProperties.getProperty(key)
    if (value != null) return value
    if (!isGithubActions && localDefault != null) return localDefault
    error("$key not set in local.properties or environment")
}

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion.majorVersion))
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.googleid)
            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            implementation(libs.compose.components.resources)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.paging.common)
            implementation(libs.androidx.paging.compose)
            implementation(libs.androidx.room.paging)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)
            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.androidx.paging.testing)
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.turbine)
        }
    }
}

android {
    namespace = appNamespace
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = appId
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = configValue(githubRunNumberKey, localDefault = "1").toInt()
        versionName = appVersionName

        val googleClientId = configValue(googleClientIdWebKey)
        val date = configValue(buildDateKey, localDefault = "local")

        buildConfigField("String", googleClientIdKey, "\"$googleClientId\"")
        buildConfigField("String", buildDateKey, "\"$date\"")
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file(releaseKeystoreFileName)
            storePassword = System.getenv(storePasswordKey)
            keyAlias = System.getenv(keyAliasKey)
            keyPassword = System.getenv(keyPasswordKey)
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    debugImplementation(libs.compose.ui.tooling)

    listOf("kspAndroid", "kspIosArm64", "kspIosSimulatorArm64").forEach {
        add(it, libs.androidx.room.compiler)
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*ComposableSingletons*",
                    $$"*$Lambda$*",
                    "socialfood.composeapp.generated.resources.*",
                    "*_Impl",
                    "*_Impl$*",
                    "$appNamespace.BuildConfig",
                    "$appNamespace.di.*",
                    "$appNamespace.data.network.model.*",
                    "$appNamespace.presentation.navigation.Route*",
                )
                annotatedBy("androidx.compose.ui.tooling.preview.Preview")
            }
        }
        total {
            html {
                onCheck = true
            }
        }
        verify {
            rule {
                minBound(minValue = 19, coverageUnits = CoverageUnit.LINE)
            }
        }
    }
}