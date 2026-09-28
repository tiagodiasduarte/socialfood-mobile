package pt.socialfood.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pt.socialfood.BuildConfig
import pt.socialfood.core.AppConfig
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.local.getDatabaseBuilder
import pt.socialfood.data.local.getRoomDatabase
import pt.socialfood.data.network.ConnectivityObserver
import pt.socialfood.data.network.ConnectivityObserverImpl
import pt.socialfood.data.repository.SettingsRepositoryImpl
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.presentation.google.GoogleSignInConfig

actual val platformModule: Module = module {
    single { AppConfig(versionName = BuildConfig.VERSION_NAME, buildDate = BuildConfig.BUILD_DATE) }
    single { GoogleSignInConfig(serverClientId = BuildConfig.GOOGLE_CLIENT_ID) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder(get())) }
    single<ConnectivityObserver> { ConnectivityObserverImpl(get()) }
}
