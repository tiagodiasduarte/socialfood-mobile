package pt.socialfood.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.data.local.entity.FAVOURITE_RESTAURANTS_REMOTE_KEYS_TABLE
import pt.socialfood.data.local.entity.FavouriteRestaurantRemoteKeyEntity

@Dao
interface FavouriteRestaurantRemoteKeyDao {

    @Upsert
    suspend fun upsert(key: FavouriteRestaurantRemoteKeyEntity)

    @Query("SELECT * FROM $FAVOURITE_RESTAURANTS_REMOTE_KEYS_TABLE WHERE scope = :scope")
    suspend fun getByScope(scope: String): FavouriteRestaurantRemoteKeyEntity?

    @Query("DELETE FROM $FAVOURITE_RESTAURANTS_REMOTE_KEYS_TABLE WHERE scope = :scope")
    suspend fun deleteByScope(scope: String)
}
