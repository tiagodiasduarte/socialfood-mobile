package pt.socialfood.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.data.local.entity.WISHLIST_RESTAURANTS_TABLE
import pt.socialfood.data.local.entity.WishlistRestaurantEntity

@Dao
interface WishlistRestaurantDao {

    @Upsert
    suspend fun upsert(wishlistRestaurant: WishlistRestaurantEntity)

    @Upsert
    suspend fun upsertAll(wishlistRestaurants: List<WishlistRestaurantEntity>)

    @Query("DELETE FROM $WISHLIST_RESTAURANTS_TABLE WHERE restaurantId = :restaurantId")
    suspend fun deleteByRestaurantId(restaurantId: String)

    @Query("DELETE FROM $WISHLIST_RESTAURANTS_TABLE WHERE restaurantId IN (:restaurantIds)")
    suspend fun deleteByRestaurantIds(restaurantIds: List<String>)

    @Query("SELECT * FROM $WISHLIST_RESTAURANTS_TABLE ORDER BY wishlistedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaged(limit: Int, offset: Int): List<WishlistRestaurantEntity>

    @Query("SELECT COUNT(*) FROM $WISHLIST_RESTAURANTS_TABLE")
    suspend fun countAll(): Int

    @Query("SELECT * FROM $WISHLIST_RESTAURANTS_TABLE WHERE restaurantId = :restaurantId LIMIT 1")
    suspend fun getByRestaurantId(restaurantId: String): WishlistRestaurantEntity?

    @Query("SELECT * FROM $WISHLIST_RESTAURANTS_TABLE WHERE syncState != 'SYNCED'")
    suspend fun getPending(): List<WishlistRestaurantEntity>

    @Query("UPDATE $WISHLIST_RESTAURANTS_TABLE SET syncState = :syncState WHERE restaurantId = :restaurantId")
    suspend fun updateSyncState(restaurantId: String, syncState: String)
}
