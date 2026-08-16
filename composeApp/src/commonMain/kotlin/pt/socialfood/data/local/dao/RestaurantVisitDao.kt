package pt.socialfood.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.data.local.entity.RESTAURANT_VISITS_TABLE
import pt.socialfood.data.local.entity.RestaurantVisitEntity

@Dao
interface RestaurantVisitDao {

    @Upsert
    suspend fun upsert(visit: RestaurantVisitEntity)

    @Upsert
    suspend fun upsertAll(visits: List<RestaurantVisitEntity>)

    @Query("DELETE FROM $RESTAURANT_VISITS_TABLE WHERE restaurantId = :restaurantId")
    suspend fun deleteByRestaurantId(restaurantId: String)

    @Query("DELETE FROM $RESTAURANT_VISITS_TABLE WHERE restaurantId IN (:restaurantIds)")
    suspend fun deleteByRestaurantIds(restaurantIds: List<String>)

    @Query(
        "SELECT * FROM $RESTAURANT_VISITS_TABLE WHERE status = :status " +
            "ORDER BY recordedAt DESC LIMIT :limit OFFSET :offset",
    )
    suspend fun getPaged(status: String, limit: Int, offset: Int): List<RestaurantVisitEntity>

    @Query("SELECT COUNT(*) FROM $RESTAURANT_VISITS_TABLE WHERE status = :status")
    suspend fun countAll(status: String): Int

    @Query("SELECT * FROM $RESTAURANT_VISITS_TABLE WHERE restaurantId = :restaurantId LIMIT 1")
    suspend fun getByRestaurantId(restaurantId: String): RestaurantVisitEntity?

    @Query("SELECT * FROM $RESTAURANT_VISITS_TABLE WHERE status = :status AND syncState != 'SYNCED'")
    suspend fun getPending(status: String): List<RestaurantVisitEntity>

    @Query("UPDATE $RESTAURANT_VISITS_TABLE SET syncState = :syncState WHERE restaurantId = :restaurantId")
    suspend fun updateSyncState(restaurantId: String, syncState: String)
}
