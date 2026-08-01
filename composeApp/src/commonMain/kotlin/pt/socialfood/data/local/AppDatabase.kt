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
import pt.socialfood.data.local.dao.HomeDao
import pt.socialfood.data.local.entity.AuthorEntity
import pt.socialfood.data.local.entity.AuthorRemoteKeyEntity
import pt.socialfood.data.local.entity.FavouriteGuideEntity
import pt.socialfood.data.local.entity.FavouriteRestaurantEntity
import pt.socialfood.data.local.entity.GuideEntity
import pt.socialfood.data.local.entity.GuideRemoteKeyEntity
import pt.socialfood.data.local.entity.HomeSectionEntity

const val DATABASE_NAME = "socialfood.db"
const val DATABASE_VERSION = 6

@Database(
    entities = [
        AuthorEntity::class,
        AuthorRemoteKeyEntity::class,
        FavouriteGuideEntity::class,
        FavouriteRestaurantEntity::class,
        GuideEntity::class,
        GuideRemoteKeyEntity::class,
        HomeSectionEntity::class,
    ],
    version = DATABASE_VERSION,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authorDao(): AuthorDao
    abstract fun authorRemoteKeyDao(): AuthorRemoteKeyDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun favouriteRestaurantDao(): FavouriteRestaurantDao
    abstract fun guideDao(): GuideDao
    abstract fun guideRemoteKeyDao(): GuideRemoteKeyDao
    abstract fun homeDao(): HomeDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
