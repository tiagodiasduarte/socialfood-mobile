package pt.socialfood.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.data.local.entity.FavouriteRestaurantEntity

@Dao
interface FavouriteRestaurantDao {

    @Upsert
    suspend fun upsert(favourite: FavouriteRestaurantEntity)

    @Upsert
    suspend fun upsertAll(favourites: List<FavouriteRestaurantEntity>)

    @Query("DELETE FROM favourite_restaurants WHERE restaurantId = :restaurantId")
    suspend fun deleteByRestaurantId(restaurantId: String)

    @Query("DELETE FROM favourite_restaurants WHERE restaurantId IN (:restaurantIds)")
    suspend fun deleteByRestaurantIds(restaurantIds: List<String>)

    @Query("SELECT * FROM favourite_restaurants ORDER BY favouritedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaged(limit: Int, offset: Int): List<FavouriteRestaurantEntity>

    @Query("SELECT COUNT(*) FROM favourite_restaurants")
    suspend fun countAll(): Int

    @Query("SELECT * FROM favourite_restaurants WHERE restaurantId = :restaurantId LIMIT 1")
    suspend fun getByRestaurantId(restaurantId: String): FavouriteRestaurantEntity?

    @Query("SELECT * FROM favourite_restaurants WHERE syncState != 'SYNCED'")
    suspend fun getPending(): List<FavouriteRestaurantEntity>

    @Query("UPDATE favourite_restaurants SET syncState = :syncState WHERE restaurantId = :restaurantId")
    suspend fun updateSyncState(restaurantId: String, syncState: String)
}
