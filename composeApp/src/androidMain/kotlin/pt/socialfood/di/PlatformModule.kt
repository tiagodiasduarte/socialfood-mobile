package pt.socialfood.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pt.socialfood.BuildConfig
import pt.socialfood.core.common.AppConfig
import pt.socialfood.feature.auth.google.GoogleSignInConfig

actual val platformModule: Module = module {
    single { AppConfig(versionName = BuildConfig.VERSION_NAME, buildDate = BuildConfig.BUILD_DATE) }
    single { GoogleSignInConfig(serverClientId = BuildConfig.GOOGLE_CLIENT_ID) }
}
