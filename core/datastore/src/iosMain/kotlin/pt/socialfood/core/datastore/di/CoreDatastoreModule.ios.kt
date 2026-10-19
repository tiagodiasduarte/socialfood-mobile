package pt.socialfood.core.datastore.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pt.socialfood.core.datastore.SettingsRepositoryImpl
import pt.socialfood.core.domain.repository.SettingsRepository

actual val coreDatastoreModule: Module = module {
    single<SettingsRepository> { SettingsRepositoryImpl() }
}
