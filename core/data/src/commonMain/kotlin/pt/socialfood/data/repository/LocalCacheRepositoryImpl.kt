package pt.socialfood.data.repository

import pt.socialfood.core.domain.repository.LocalCacheRepository
import pt.socialfood.data.local.AppDatabase

internal class LocalCacheRepositoryImpl(private val appDatabase: AppDatabase) : LocalCacheRepository {

    override suspend fun clearAll() {
        appDatabase.authorDao().deleteAll()
        appDatabase.authorRemoteKeyDao().deleteAll()
        appDatabase.favouriteDao().deleteAll()
        appDatabase.favouriteGuideRemoteKeyDao().deleteAll()
        appDatabase.favouriteRestaurantDao().deleteAll()
        appDatabase.favouriteRestaurantRemoteKeyDao().deleteAll()
        appDatabase.guideDao().deleteAll()
        appDatabase.guideRemoteKeyDao().deleteAll()
        appDatabase.homeDao().deleteAll()
        appDatabase.restaurantVisitStatusDao().deleteAll()
        appDatabase.restaurantVisitStatusRemoteKeyDao().deleteAll()
    }
}
