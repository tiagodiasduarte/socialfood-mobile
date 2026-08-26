package pt.socialfood.fakes

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.sqlite.SQLiteException
import pt.socialfood.data.local.dao.FavouriteRestaurantDao
import pt.socialfood.data.local.entity.FavouriteRestaurantEntity

class FakeFavouriteRestaurantDao(
    private val shouldThrowOnWrite: Boolean = false,
    initialEntities: List<FavouriteRestaurantEntity> = emptyList(),
) : FavouriteRestaurantDao {

    private val entities =
        LinkedHashMap<String, FavouriteRestaurantEntity>(initialEntities.associateBy { it.restaurantId })

    fun getAll(): List<FavouriteRestaurantEntity> = entities.values.toList()

    override suspend fun upsert(favourite: FavouriteRestaurantEntity) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities[favourite.restaurantId] = favourite
    }

    override suspend fun upsertAll(favourites: List<FavouriteRestaurantEntity>) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        favourites.forEach { entities[it.restaurantId] = it }
    }

    override suspend fun deleteByRestaurantId(restaurantId: String) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities.remove(restaurantId)
    }

    override suspend fun deleteByRestaurantIds(restaurantIds: List<String>) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        restaurantIds.forEach { entities.remove(it) }
    }

    override suspend fun deleteAll() {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities.clear()
    }

    override fun pagingSource(): PagingSource<Int, FavouriteRestaurantEntity> = FakeFavouriteRestaurantPagingSource {
        entities.values
            .filter { it.syncState != "PENDING_REMOVE" }
            .sortedBy { it.position }
    }

    override suspend fun getByRestaurantId(restaurantId: String): FavouriteRestaurantEntity? = entities[restaurantId]

    override suspend fun getPending(): List<FavouriteRestaurantEntity> =
        entities.values.filter { it.syncState != "SYNCED" }

    override suspend fun updateSyncState(restaurantId: String, syncState: String) {
        entities[restaurantId]?.let { entities[restaurantId] = it.copy(syncState = syncState) }
    }
}

private class FakeFavouriteRestaurantPagingSource(private val loadEntities: () -> List<FavouriteRestaurantEntity>) :
    PagingSource<Int, FavouriteRestaurantEntity>() {

    override fun getRefreshKey(state: PagingState<Int, FavouriteRestaurantEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FavouriteRestaurantEntity> {
        val offset = params.key ?: 0
        val all = loadEntities()
        val data = all.drop(offset).take(params.loadSize)
        val nextKey = if (offset + data.size >= all.size) null else offset + data.size
        val prevKey = if (offset == 0) null else maxOf(0, offset - params.loadSize)
        return LoadResult.Page(data = data, prevKey = prevKey, nextKey = nextKey)
    }
}
