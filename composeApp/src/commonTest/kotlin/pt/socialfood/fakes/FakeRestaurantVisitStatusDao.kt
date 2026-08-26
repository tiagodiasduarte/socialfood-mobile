package pt.socialfood.fakes

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.sqlite.SQLiteException
import pt.socialfood.data.local.dao.RestaurantVisitStatusDao
import pt.socialfood.data.local.entity.RestaurantVisitStatusEntity

class FakeRestaurantVisitStatusDao(
    private val shouldThrowOnWrite: Boolean = false,
    initialEntities: List<RestaurantVisitStatusEntity> = emptyList(),
) : RestaurantVisitStatusDao {

    private val entities = LinkedHashMap<String, RestaurantVisitStatusEntity>(
        initialEntities.associateBy { it.restaurantId },
    )

    fun getAll(): List<RestaurantVisitStatusEntity> = entities.values.toList()

    override suspend fun upsert(visit: RestaurantVisitStatusEntity) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities[visit.restaurantId] = visit
    }

    override suspend fun upsertAll(visits: List<RestaurantVisitStatusEntity>) {
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

    override fun pagingSource(status: String): PagingSource<Int, RestaurantVisitStatusEntity> =
        FakeRestaurantVisitStatusPagingSource {
            entities.values
                .filter { it.status == status && it.syncState != "PENDING_REMOVE" }
                .sortedBy { it.position }
        }

    override suspend fun deleteByStatus(status: String) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities.values.filter { it.status == status }.forEach { entities.remove(it.restaurantId) }
    }

    override suspend fun getByRestaurantId(restaurantId: String): RestaurantVisitStatusEntity? = entities[restaurantId]

    override suspend fun getPending(): List<RestaurantVisitStatusEntity> =
        entities.values.filter { it.syncState != "SYNCED" }

    override suspend fun updateSyncState(restaurantId: String, syncState: String) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities[restaurantId]?.let { entities[restaurantId] = it.copy(syncState = syncState) }
    }

    override suspend fun updateSyncState(restaurantIds: List<String>, syncState: String) {
        restaurantIds.forEach { updateSyncState(it, syncState) }
    }
}

private class FakeRestaurantVisitStatusPagingSource(private val loadEntities: () -> List<RestaurantVisitStatusEntity>) :
    PagingSource<Int, RestaurantVisitStatusEntity>() {

    override fun getRefreshKey(state: PagingState<Int, RestaurantVisitStatusEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RestaurantVisitStatusEntity> {
        val offset = params.key ?: 0
        val all = loadEntities()
        val data = all.drop(offset).take(params.loadSize)
        val nextKey = if (offset + data.size >= all.size) null else offset + data.size
        val prevKey = if (offset == 0) null else maxOf(0, offset - params.loadSize)
        return LoadResult.Page(data = data, prevKey = prevKey, nextKey = nextKey)
    }
}
