package pt.socialfood.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

const val DATABASE_NAME = "socialfood.db"

/**
 * Shared finishing step for the platform-specific [RoomDatabase.Builder] created in each
 * platform's `di/PlatformModule.kt` (Android needs a `Context` to build its builder, iOS doesn't —
 * so the builder itself is constructed per-platform rather than via `expect`/`actual`).
 */
fun getRoomDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
