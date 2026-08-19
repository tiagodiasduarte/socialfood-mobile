package pt.socialfood.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.data.local.entity.RESTAURANT_VISIT_STATUS_REMOTE_KEYS_TABLE
import pt.socialfood.data.local.entity.RestaurantVisitStatusRemoteKeyEntity

@Dao
interface RestaurantVisitStatusRemoteKeyDao {

    @Upsert
    suspend fun upsert(key: RestaurantVisitStatusRemoteKeyEntity)

    @Query("SELECT * FROM $RESTAURANT_VISIT_STATUS_REMOTE_KEYS_TABLE WHERE scope = :scope")
    suspend fun getByScope(scope: String): RestaurantVisitStatusRemoteKeyEntity?

    @Query("DELETE FROM $RESTAURANT_VISIT_STATUS_REMOTE_KEYS_TABLE WHERE scope = :scope")
    suspend fun deleteByScope(scope: String)
}
