package pt.socialfood.fakes

import pt.socialfood.data.local.dao.FavouriteRestaurantDao
import pt.socialfood.data.local.entity.FavouriteRestaurantEntity

class FakeFavouriteRestaurantDao(private val shouldThrowOnWrite: Boolean = false) : FavouriteRestaurantDao {

    private val entities = LinkedHashMap<String, FavouriteRestaurantEntity>()

    override suspend fun upsert(favourite: FavouriteRestaurantEntity) {
        if (shouldThrowOnWrite) throw RuntimeException("test error")
        entities[favourite.restaurantId] = favourite
    }

    override suspend fun upsertAll(favourites: List<FavouriteRestaurantEntity>) {
        if (shouldThrowOnWrite) throw RuntimeException("test error")
        favourites.forEach { entities[it.restaurantId] = it }
    }

    override suspend fun deleteByRestaurantId(restaurantId: String) {
        if (shouldThrowOnWrite) throw RuntimeException("test error")
        entities.remove(restaurantId)
    }

    override suspend fun deleteByRestaurantIds(restaurantIds: List<String>) {
        if (shouldThrowOnWrite) throw RuntimeException("test error")
        restaurantIds.forEach { entities.remove(it) }
    }

    override suspend fun getPaged(limit: Int, offset: Int): List<FavouriteRestaurantEntity> =
        entities.values
            .sortedByDescending { it.favouritedAt }
            .drop(offset)
            .take(limit)

    override suspend fun countAll(): Int = entities.size

    override suspend fun getByRestaurantId(restaurantId: String): FavouriteRestaurantEntity? = entities[restaurantId]

    override suspend fun getPending(): List<FavouriteRestaurantEntity> =
        entities.values.filter { it.syncState != "SYNCED" }

    override suspend fun updateSyncState(restaurantId: String, syncState: String) {
        entities[restaurantId]?.let { entities[restaurantId] = it.copy(syncState = syncState) }
    }
}
