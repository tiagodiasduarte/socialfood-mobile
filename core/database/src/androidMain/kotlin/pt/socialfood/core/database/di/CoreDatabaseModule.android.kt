package pt.socialfood.core.database.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.local.getDatabaseBuilder
import pt.socialfood.data.local.getRoomDatabase

actual val coreDatabaseModule: Module = module {
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder(get())) }
}
