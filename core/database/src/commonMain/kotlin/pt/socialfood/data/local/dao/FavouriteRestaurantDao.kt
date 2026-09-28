package pt.socialfood.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.data.local.entity.FAVOURITE_RESTAURANTS_TABLE
import pt.socialfood.data.local.entity.FavouriteRestaurantEntity

@Dao
interface FavouriteRestaurantDao {

    @Upsert
    suspend fun upsert(favourite: FavouriteRestaurantEntity)

    @Upsert
    suspend fun upsertAll(favourites: List<FavouriteRestaurantEntity>)

    @Query("DELETE FROM $FAVOURITE_RESTAURANTS_TABLE WHERE restaurantId = :restaurantId")
    suspend fun deleteByRestaurantId(restaurantId: String)

    @Query("DELETE FROM $FAVOURITE_RESTAURANTS_TABLE WHERE restaurantId IN (:restaurantIds)")
    suspend fun deleteByRestaurantIds(restaurantIds: List<String>)

    @Query("DELETE FROM $FAVOURITE_RESTAURANTS_TABLE")
    suspend fun deleteAll()

    @Query(
        "SELECT * FROM $FAVOURITE_RESTAURANTS_TABLE WHERE syncState != 'PENDING_REMOVE' ORDER BY position ASC",
    )
    fun pagingSource(): PagingSource<Int, FavouriteRestaurantEntity>

    @Query("SELECT * FROM $FAVOURITE_RESTAURANTS_TABLE WHERE restaurantId = :restaurantId LIMIT 1")
    suspend fun getByRestaurantId(restaurantId: String): FavouriteRestaurantEntity?

    @Query("SELECT * FROM $FAVOURITE_RESTAURANTS_TABLE WHERE syncState != 'SYNCED'")
    suspend fun getPending(): List<FavouriteRestaurantEntity>

    @Query("UPDATE $FAVOURITE_RESTAURANTS_TABLE SET syncState = :syncState WHERE restaurantId = :restaurantId")
    suspend fun updateSyncState(restaurantId: String, syncState: String)
}
