package pt.socialfood.di

import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSBundle
import pt.socialfood.core.AppConfig
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.local.getDatabaseBuilder
import pt.socialfood.data.local.getRoomDatabase
import pt.socialfood.data.network.ConnectivityObserver
import pt.socialfood.data.network.ConnectivityObserverImpl
import pt.socialfood.data.repository.SettingsRepositoryImpl
import pt.socialfood.domain.repository.SettingsRepository

actual val platformModule: Module = module {
    single {
        val info = NSBundle.mainBundle.infoDictionary
        AppConfig(
            versionName = info?.get("CFBundleShortVersionString") as? String ?: "",
            buildDate = info?.get("BuildDate") as? String ?: "",
        )
    }
    single<SettingsRepository> { SettingsRepositoryImpl() }
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder()) }
    single<ConnectivityObserver> { ConnectivityObserverImpl() }
}
