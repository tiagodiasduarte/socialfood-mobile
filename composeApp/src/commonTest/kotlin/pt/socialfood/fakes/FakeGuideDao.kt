package pt.socialfood.fakes

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pt.socialfood.data.local.dao.GuideDao
import pt.socialfood.data.local.entity.GuideEntity

class FakeGuideDao(private val shouldThrowOnWrite: Boolean = false) : GuideDao {

    private val entities = mutableListOf<GuideEntity>()

    fun getAll(): List<GuideEntity> = entities.toList()

    override suspend fun upsertAll(guides: List<GuideEntity>) {
        if (shouldThrowOnWrite) throw FakeException("test error")
        guides.forEach { guide ->
            val index = entities.indexOfFirst { it.id == guide.id && it.scope == guide.scope }
            if (index >= 0) entities[index] = guide else entities.add(guide)
        }
    }

    override suspend fun deleteByScope(scope: String) {
        if (shouldThrowOnWrite) throw FakeException("test error")
        entities.removeAll { it.scope == scope }
    }

    override fun pagingSource(scope: String): PagingSource<Int, GuideEntity> =
        FakeGuidePagingSource { entities.filter { it.scope == scope }.sortedBy { it.position } }
}

private class FakeGuidePagingSource(
    private val loadEntities: () -> List<GuideEntity>,
) : PagingSource<Int, GuideEntity>() {

    override fun getRefreshKey(state: PagingState<Int, GuideEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GuideEntity> {
        val offset = params.key ?: 0
        val all = loadEntities()
        val data = all.drop(offset).take(params.loadSize)
        val nextKey = if (offset + data.size >= all.size) null else offset + data.size
        val prevKey = if (offset == 0) null else maxOf(0, offset - params.loadSize)
        return LoadResult.Page(data = data, prevKey = prevKey, nextKey = nextKey)
    }
}
