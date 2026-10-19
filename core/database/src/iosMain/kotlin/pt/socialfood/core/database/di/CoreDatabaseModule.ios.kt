package pt.socialfood.core.database.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pt.socialfood.core.database.AppDatabase
import pt.socialfood.core.database.getDatabaseBuilder
import pt.socialfood.core.database.getRoomDatabase

actual val coreDatabaseModule: Module = module {
    single<AppDatabase> { getRoomDatabase(getDatabaseBuilder()) }
}
