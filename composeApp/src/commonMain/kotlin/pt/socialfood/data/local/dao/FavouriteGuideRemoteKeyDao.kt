package pt.socialfood.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.data.local.entity.FAVOURITE_GUIDES_REMOTE_KEYS_TABLE
import pt.socialfood.data.local.entity.FavouriteGuideRemoteKeyEntity

@Dao
interface FavouriteGuideRemoteKeyDao {

    @Upsert
    suspend fun upsert(key: FavouriteGuideRemoteKeyEntity)

    @Query("SELECT * FROM $FAVOURITE_GUIDES_REMOTE_KEYS_TABLE WHERE scope = :scope")
    suspend fun getByScope(scope: String): FavouriteGuideRemoteKeyEntity?

    @Query("DELETE FROM $FAVOURITE_GUIDES_REMOTE_KEYS_TABLE WHERE scope = :scope")
    suspend fun deleteByScope(scope: String)
}
