package pt.socialfood.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.core.database.entity.FAVOURITE_RESTAURANTS_REMOTE_KEYS_TABLE
import pt.socialfood.core.database.entity.FavouriteRestaurantRemoteKeyEntity

@Dao
interface FavouriteRestaurantRemoteKeyDao {

    @Upsert
    suspend fun upsert(key: FavouriteRestaurantRemoteKeyEntity)

    @Query("SELECT * FROM $FAVOURITE_RESTAURANTS_REMOTE_KEYS_TABLE WHERE scope = :scope")
    suspend fun getByScope(scope: String): FavouriteRestaurantRemoteKeyEntity?

    @Query("DELETE FROM $FAVOURITE_RESTAURANTS_REMOTE_KEYS_TABLE WHERE scope = :scope")
    suspend fun deleteByScope(scope: String)

    @Query("DELETE FROM $FAVOURITE_RESTAURANTS_REMOTE_KEYS_TABLE")
    suspend fun deleteAll()
}
