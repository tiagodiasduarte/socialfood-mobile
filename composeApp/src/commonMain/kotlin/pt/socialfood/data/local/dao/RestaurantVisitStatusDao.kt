package pt.socialfood.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.data.local.entity.RESTAURANT_VISIT_STATUS_TABLE
import pt.socialfood.data.local.entity.RestaurantVisitStatusEntity

@Dao
@Suppress("TooManyFunctions")
interface RestaurantVisitStatusDao {

    @Upsert
    suspend fun upsert(visit: RestaurantVisitStatusEntity)

    @Upsert
    suspend fun upsertAll(visits: List<RestaurantVisitStatusEntity>)

    @Query("DELETE FROM $RESTAURANT_VISIT_STATUS_TABLE WHERE restaurantId = :restaurantId")
    suspend fun deleteByRestaurantId(restaurantId: String)

    @Query("DELETE FROM $RESTAURANT_VISIT_STATUS_TABLE WHERE restaurantId IN (:restaurantIds)")
    suspend fun deleteByRestaurantIds(restaurantIds: List<String>)

    @Query(
        "SELECT * FROM $RESTAURANT_VISIT_STATUS_TABLE WHERE status = :status AND syncState != 'PENDING_REMOVE' " +
            "ORDER BY position ASC",
    )
    fun pagingSource(status: String): PagingSource<Int, RestaurantVisitStatusEntity>

    @Query("DELETE FROM $RESTAURANT_VISIT_STATUS_TABLE WHERE status = :status")
    suspend fun deleteByStatus(status: String)

    @Query(
        "SELECT * FROM $RESTAURANT_VISIT_STATUS_TABLE WHERE status = :status AND syncState != 'PENDING_REMOVE' " +
            "ORDER BY recordedAt DESC LIMIT :limit OFFSET :offset",
    )
    suspend fun getPaged(status: String, limit: Int, offset: Int): List<RestaurantVisitStatusEntity>

    @Query(
        "SELECT COUNT(*) FROM $RESTAURANT_VISIT_STATUS_TABLE WHERE status = :status AND syncState != 'PENDING_REMOVE'",
    )
    suspend fun countAll(status: String): Int

    @Query("SELECT * FROM $RESTAURANT_VISIT_STATUS_TABLE WHERE restaurantId = :restaurantId LIMIT 1")
    suspend fun getByRestaurantId(restaurantId: String): RestaurantVisitStatusEntity?

    @Query("SELECT * FROM $RESTAURANT_VISIT_STATUS_TABLE WHERE syncState != 'SYNCED'")
    suspend fun getPending(): List<RestaurantVisitStatusEntity>

    @Query("UPDATE $RESTAURANT_VISIT_STATUS_TABLE SET syncState = :syncState WHERE restaurantId = :restaurantId")
    suspend fun updateSyncState(restaurantId: String, syncState: String)

    @Query("UPDATE $RESTAURANT_VISIT_STATUS_TABLE SET syncState = :syncState WHERE restaurantId IN (:restaurantIds)")
    suspend fun updateSyncState(restaurantIds: List<String>, syncState: String)
}
