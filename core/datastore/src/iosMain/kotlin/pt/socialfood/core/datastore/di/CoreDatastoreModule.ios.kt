package pt.socialfood.core.datastore.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pt.socialfood.data.repository.SettingsRepositoryImpl
import pt.socialfood.domain.repository.SettingsRepository

actual val coreDatastoreModule: Module = module {
    single<SettingsRepository> { SettingsRepositoryImpl() }
}
