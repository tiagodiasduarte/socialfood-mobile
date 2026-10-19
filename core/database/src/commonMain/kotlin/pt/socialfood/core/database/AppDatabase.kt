package pt.socialfood.core.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import pt.socialfood.core.database.dao.AuthorDao
import pt.socialfood.core.database.dao.AuthorRemoteKeyDao
import pt.socialfood.core.database.dao.FavouriteDao
import pt.socialfood.core.database.dao.FavouriteGuideRemoteKeyDao
import pt.socialfood.core.database.dao.FavouriteRestaurantDao
import pt.socialfood.core.database.dao.FavouriteRestaurantRemoteKeyDao
import pt.socialfood.core.database.dao.GuideDao
import pt.socialfood.core.database.dao.GuideRemoteKeyDao
import pt.socialfood.core.database.dao.HomeDao
import pt.socialfood.core.database.dao.RestaurantVisitStatusDao
import pt.socialfood.core.database.dao.RestaurantVisitStatusRemoteKeyDao
import pt.socialfood.core.database.entity.AuthorEntity
import pt.socialfood.core.database.entity.AuthorRemoteKeyEntity
import pt.socialfood.core.database.entity.FavouriteGuideEntity
import pt.socialfood.core.database.entity.FavouriteGuideRemoteKeyEntity
import pt.socialfood.core.database.entity.FavouriteRestaurantEntity
import pt.socialfood.core.database.entity.FavouriteRestaurantRemoteKeyEntity
import pt.socialfood.core.database.entity.GuideEntity
import pt.socialfood.core.database.entity.GuideRemoteKeyEntity
import pt.socialfood.core.database.entity.HomeSectionEntity
import pt.socialfood.core.database.entity.RestaurantVisitStatusEntity
import pt.socialfood.core.database.entity.RestaurantVisitStatusRemoteKeyEntity

const val DATABASE_NAME = "socialfood.db"
const val DATABASE_VERSION = 1

@Database(
    entities = [
        AuthorEntity::class,
        AuthorRemoteKeyEntity::class,
        FavouriteGuideEntity::class,
        FavouriteGuideRemoteKeyEntity::class,
        FavouriteRestaurantEntity::class,
        FavouriteRestaurantRemoteKeyEntity::class,
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
@Suppress("TooManyFunctions")
abstract class AppDatabase : RoomDatabase() {
    abstract fun authorDao(): AuthorDao
    abstract fun authorRemoteKeyDao(): AuthorRemoteKeyDao
    abstract fun favouriteDao(): FavouriteDao
    abstract fun favouriteGuideRemoteKeyDao(): FavouriteGuideRemoteKeyDao
    abstract fun favouriteRestaurantDao(): FavouriteRestaurantDao
    abstract fun favouriteRestaurantRemoteKeyDao(): FavouriteRestaurantRemoteKeyDao
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
