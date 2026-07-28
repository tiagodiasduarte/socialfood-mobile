package pt.socialfood.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.data.local.entity.GUIDE_REMOTE_KEYS_TABLE
import pt.socialfood.data.local.entity.GuideRemoteKeyEntity

@Dao
interface GuideRemoteKeyDao {

    @Upsert
    suspend fun upsert(key: GuideRemoteKeyEntity)

    @Query("SELECT * FROM $GUIDE_REMOTE_KEYS_TABLE WHERE scope = :scope")
    suspend fun getByScope(scope: String): GuideRemoteKeyEntity?

    @Query("DELETE FROM $GUIDE_REMOTE_KEYS_TABLE WHERE scope = :scope")
    suspend fun deleteByScope(scope: String)
}
