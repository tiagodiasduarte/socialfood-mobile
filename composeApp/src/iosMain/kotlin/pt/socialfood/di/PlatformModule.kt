package pt.socialfood.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.local.getDatabaseBuilder
import pt.socialfood.data.local.getRoomDatabase
import pt.socialfood.data.repository.SettingsRepositoryImpl
import pt.socialfood.domain.repository.SettingsRepository

actual val platformModule: Module = module {
    single<SettingsRepository> { SettingsRepositoryImpl() }
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder()) }
}
