package pt.socialfood.core.datastore.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pt.socialfood.core.domain.repository.SettingsRepository
import pt.socialfood.data.repository.SettingsRepositoryImpl

actual val coreDatastoreModule: Module = module {
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}
