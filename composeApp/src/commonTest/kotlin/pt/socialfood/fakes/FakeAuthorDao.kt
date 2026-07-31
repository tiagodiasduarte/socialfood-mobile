package pt.socialfood.fakes

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pt.socialfood.data.local.dao.AuthorDao
import pt.socialfood.data.local.entity.AuthorEntity

class FakeAuthorDao(private val shouldThrowOnWrite: Boolean = false) : AuthorDao {

    private val entities = mutableListOf<AuthorEntity>()

    fun getAll(): List<AuthorEntity> = entities.toList()

    override suspend fun upsertAll(authors: List<AuthorEntity>) {
        if (shouldThrowOnWrite) throw FakeException("test error")
        authors.forEach { author ->
            val index = entities.indexOfFirst { it.id == author.id }
            if (index >= 0) entities[index] = author else entities.add(author)
        }
    }

    override suspend fun deleteAll() {
        if (shouldThrowOnWrite) throw FakeException("test error")
        entities.clear()
    }

    override fun pagingSource(): PagingSource<Int, AuthorEntity> =
        FakeAuthorPagingSource { entities.sortedBy { it.position } }
}

private class FakeAuthorPagingSource(
    private val loadEntities: () -> List<AuthorEntity>,
) : PagingSource<Int, AuthorEntity>() {

    override fun getRefreshKey(state: PagingState<Int, AuthorEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AuthorEntity> {
        val offset = params.key ?: 0
        val all = loadEntities()
        val data = all.drop(offset).take(params.loadSize)
        val nextKey = if (offset + data.size >= all.size) null else offset + data.size
        val prevKey = if (offset == 0) null else maxOf(0, offset - params.loadSize)
        return LoadResult.Page(data = data, prevKey = prevKey, nextKey = nextKey)
    }
}
