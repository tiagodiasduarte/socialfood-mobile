package pt.socialfood.fakes

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.socialfood.data.local.dao.FavouriteDao
import pt.socialfood.data.local.entity.FavouriteGuideEntity

class FakeFavouriteDao(
    private val shouldThrowOnWrite: Boolean = false,
    initialEntities: List<FavouriteGuideEntity> = emptyList(),
) : FavouriteDao {

    private val entities = LinkedHashMap<String, FavouriteGuideEntity>(initialEntities.associateBy { it.guideId })
    private val idsFlow = MutableStateFlow(entities.keys.toList())

    fun getAll(): List<FavouriteGuideEntity> = entities.values.toList()

    override suspend fun upsert(favourite: FavouriteGuideEntity) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities[favourite.guideId] = favourite
        idsFlow.value = entities.keys.toList()
    }

    override suspend fun upsertAll(favourites: List<FavouriteGuideEntity>) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        favourites.forEach { entities[it.guideId] = it }
        idsFlow.value = entities.keys.toList()
    }

    override suspend fun deleteByGuideId(guideId: String) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities.remove(guideId)
        idsFlow.value = entities.keys.toList()
    }

    override suspend fun deleteByGuideIds(guideIds: List<String>) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        guideIds.forEach { entities.remove(it) }
        idsFlow.value = entities.keys.toList()
    }

    override suspend fun deleteAll() {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        entities.clear()
        idsFlow.value = emptyList()
    }

    override fun observeAllIds(): Flow<List<String>> = idsFlow

    override fun pagingSource(): PagingSource<Int, FavouriteGuideEntity> = FakeFavouriteGuidePagingSource {
        entities.values
            .filter { it.syncState != "PENDING_REMOVE" }
            .sortedBy { it.position }
    }

    override suspend fun getByGuideId(guideId: String): FavouriteGuideEntity? = entities[guideId]

    override suspend fun getPending(): List<FavouriteGuideEntity> = entities.values.filter { it.syncState != "SYNCED" }

    override suspend fun updateSyncState(guideId: String, syncState: String) {
        entities[guideId]?.let { entities[guideId] = it.copy(syncState = syncState) }
    }
}

private class FakeFavouriteGuidePagingSource(private val loadEntities: () -> List<FavouriteGuideEntity>) :
    PagingSource<Int, FavouriteGuideEntity>() {

    override fun getRefreshKey(state: PagingState<Int, FavouriteGuideEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FavouriteGuideEntity> {
        val offset = params.key ?: 0
        val all = loadEntities()
        val data = all.drop(offset).take(params.loadSize)
        val nextKey = if (offset + data.size >= all.size) null else offset + data.size
        val prevKey = if (offset == 0) null else maxOf(0, offset - params.loadSize)
        return LoadResult.Page(data = data, prevKey = prevKey, nextKey = nextKey)
    }
}
