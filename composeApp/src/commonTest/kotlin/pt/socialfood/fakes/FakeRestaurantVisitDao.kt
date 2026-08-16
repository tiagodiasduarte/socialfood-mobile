package pt.socialfood.fakes

import androidx.sqlite.SQLiteException
import pt.socialfood.data.local.dao.RestaurantVisitDao
import pt.socialfood.data.local.entity.RestaurantVisitEntity

class FakeRestaurantVisitDao(private val shouldThrowOnWrite: Boolean = false) : RestaurantVisitDao {

    private val entities = LinkedHashMap<String, RestaurantVisitEntity>()

    override suspend fun upsert(visit: RestaurantVisitEntity) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities[visit.restaurantId] = visit
    }

    override suspend fun upsertAll(visits: List<RestaurantVisitEntity>) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        visits.forEach { entities[it.restaurantId] = it }
    }

    override suspend fun deleteByRestaurantId(restaurantId: String) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities.remove(restaurantId)
    }

    override suspend fun deleteByRestaurantIds(restaurantIds: List<String>) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        restaurantIds.forEach { entities.remove(it) }
    }

    override suspend fun getPaged(status: String, limit: Int, offset: Int): List<RestaurantVisitEntity> =
        entities.values
            .filter { it.status == status }
            .sortedByDescending { it.recordedAt }
            .drop(offset)
            .take(limit)

    override suspend fun countAll(status: String): Int = entities.values.count { it.status == status }

    override suspend fun getByRestaurantId(restaurantId: String): RestaurantVisitEntity? = entities[restaurantId]

    override suspend fun getPending(status: String): List<RestaurantVisitEntity> =
        entities.values.filter { it.status == status && it.syncState != "SYNCED" }

    override suspend fun updateSyncState(restaurantId: String, syncState: String) {
        entities[restaurantId]?.let { entities[restaurantId] = it.copy(syncState = syncState) }
    }
}
