package pt.socialfood.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import pt.socialfood.data.local.dao.AuthorDao
import pt.socialfood.data.local.dao.AuthorRemoteKeyDao
import pt.socialfood.data.local.dao.FavouriteDao
import pt.socialfood.data.local.dao.FavouriteRestaurantDao
import pt.socialfood.data.local.dao.GuideDao
import pt.socialfood.data.local.dao.GuideRemoteKeyDao
import pt.socialfood.data.local.entity.AuthorEntity
import pt.socialfood.data.local.entity.AuthorRemoteKeyEntity
import pt.socialfood.data.local.entity.FavouriteGuideEntity
import pt.socialfood.data.local.entity.FavouriteRestaurantEntity
import pt.socialfood.data.local.entity.GuideEntity
import pt.socialfood.data.local.entity.GuideRemoteKeyEntity

const val DATABASE_VERSION = 4

@Database(
    entities = [
        FavouriteGuideEntity::class,
        FavouriteRestaurantEntity::class,
        GuideEntity::class,
        GuideRemoteKeyEntity::class,
        AuthorEntity::class,
        AuthorRemoteKeyEntity::class,
    ],
    version = DATABASE_VERSION,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao
    abstract fun favouriteRestaurantDao(): FavouriteRestaurantDao
    abstract fun guideDao(): GuideDao
    abstract fun guideRemoteKeyDao(): GuideRemoteKeyDao
    abstract fun authorDao(): AuthorDao
    abstract fun authorRemoteKeyDao(): AuthorRemoteKeyDao
}

// The `expect object : RoomDatabaseConstructor` pattern below is the standard KMP-Room
// mechanism Room's KSP compiler uses to generate the `actual` implementation of `initialize()`
// per target (Android/iOS) — see https://developer.android.com/kotlin/multiplatform/room.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
