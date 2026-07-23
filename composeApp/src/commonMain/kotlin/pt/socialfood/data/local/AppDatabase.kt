package pt.socialfood.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import pt.socialfood.data.local.dao.FavouriteDao
import pt.socialfood.data.local.entity.FavouriteGuideEntity

@Database(entities = [FavouriteGuideEntity::class], version = 1, exportSchema = true)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao
}

// The `expect object : RoomDatabaseConstructor` pattern below is the standard KMP-Room
// mechanism Room's KSP compiler uses to generate the `actual` implementation of `initialize()`
// per target (Android/iOS) — see https://developer.android.com/kotlin/multiplatform/room.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
