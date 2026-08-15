package pt.socialfood.fakes

import androidx.sqlite.SQLiteException
import pt.socialfood.data.local.dao.WishlistRestaurantDao
import pt.socialfood.data.local.entity.WishlistRestaurantEntity

class FakeWishlistRestaurantDao(private val shouldThrowOnWrite: Boolean = false) : WishlistRestaurantDao {

    private val entities = LinkedHashMap<String, WishlistRestaurantEntity>()

    override suspend fun upsert(wishlistRestaurant: WishlistRestaurantEntity) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities[wishlistRestaurant.restaurantId] = wishlistRestaurant
    }

    override suspend fun upsertAll(wishlistRestaurants: List<WishlistRestaurantEntity>) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        wishlistRestaurants.forEach { entities[it.restaurantId] = it }
    }

    override suspend fun deleteByRestaurantId(restaurantId: String) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities.remove(restaurantId)
    }

    override suspend fun deleteByRestaurantIds(restaurantIds: List<String>) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        restaurantIds.forEach { entities.remove(it) }
    }

    override suspend fun getPaged(limit: Int, offset: Int): List<WishlistRestaurantEntity> = entities.values
        .sortedByDescending { it.wishlistedAt }
        .drop(offset)
        .take(limit)

    override suspend fun countAll(): Int = entities.size

    override suspend fun getByRestaurantId(restaurantId: String): WishlistRestaurantEntity? = entities[restaurantId]

    override suspend fun getPending(): List<WishlistRestaurantEntity> =
        entities.values.filter { it.syncState != "SYNCED" }

    override suspend fun updateSyncState(restaurantId: String, syncState: String) {
        entities[restaurantId]?.let { entities[restaurantId] = it.copy(syncState = syncState) }
    }
}
