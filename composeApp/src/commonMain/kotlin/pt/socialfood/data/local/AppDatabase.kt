package pt.socialfood.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import pt.socialfood.data.local.dao.AuthorDao
import pt.socialfood.data.local.dao.AuthorRemoteKeyDao
import pt.socialfood.data.local.dao.FavouriteDao
import pt.socialfood.data.local.dao.FavouriteGuideRemoteKeyDao
import pt.socialfood.data.local.dao.FavouriteRestaurantDao
import pt.socialfood.data.local.dao.GuideDao
import pt.socialfood.data.local.dao.GuideRemoteKeyDao
import pt.socialfood.data.local.dao.HomeDao
import pt.socialfood.data.local.dao.RestaurantVisitStatusDao
import pt.socialfood.data.local.dao.RestaurantVisitStatusRemoteKeyDao
import pt.socialfood.data.local.entity.AuthorEntity
import pt.socialfood.data.local.entity.AuthorRemoteKeyEntity
import pt.socialfood.data.local.entity.FavouriteGuideEntity
import pt.socialfood.data.local.entity.FavouriteGuideRemoteKeyEntity
import pt.socialfood.data.local.entity.FavouriteRestaurantEntity
import pt.socialfood.data.local.entity.GuideEntity
import pt.socialfood.data.local.entity.GuideRemoteKeyEntity
import pt.socialfood.data.local.entity.HomeSectionEntity
import pt.socialfood.data.local.entity.RestaurantVisitStatusEntity
import pt.socialfood.data.local.entity.RestaurantVisitStatusRemoteKeyEntity

const val DATABASE_NAME = "socialfood.db"
const val DATABASE_VERSION = 10

@Database(
    entities = [
        AuthorEntity::class,
        AuthorRemoteKeyEntity::class,
        FavouriteGuideEntity::class,
        FavouriteGuideRemoteKeyEntity::class,
        FavouriteRestaurantEntity::class,
        GuideEntity::class,
        GuideRemoteKeyEntity::class,
        HomeSectionEntity::class,
        RestaurantVisitStatusEntity::class,
        RestaurantVisitStatusRemoteKeyEntity::class,
    ],
    version = DATABASE_VERSION,
    exportSchema = true,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authorDao(): AuthorDao
    abstract fun authorRemoteKeyDao(): AuthorRemoteKeyDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun favouriteGuideRemoteKeyDao(): FavouriteGuideRemoteKeyDao
    abstract fun favouriteRestaurantDao(): FavouriteRestaurantDao
    abstract fun guideDao(): GuideDao
    abstract fun guideRemoteKeyDao(): GuideRemoteKeyDao
    abstract fun homeDao(): HomeDao
    abstract fun restaurantVisitStatusDao(): RestaurantVisitStatusDao
    abstract fun restaurantVisitStatusRemoteKeyDao(): RestaurantVisitStatusRemoteKeyDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
