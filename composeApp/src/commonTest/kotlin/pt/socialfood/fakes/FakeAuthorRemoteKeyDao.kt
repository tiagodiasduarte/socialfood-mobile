package pt.socialfood.fakes

import androidx.sqlite.SQLiteException
import pt.socialfood.data.local.dao.AuthorRemoteKeyDao
import pt.socialfood.data.local.entity.AuthorRemoteKeyEntity

class FakeAuthorRemoteKeyDao(private val shouldThrowOnWrite: Boolean = false) : AuthorRemoteKeyDao {

    private var key: AuthorRemoteKeyEntity? = null

    override suspend fun upsert(key: AuthorRemoteKeyEntity) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        this.key = key
    }

    override suspend fun get(): AuthorRemoteKeyEntity? = key

    override suspend fun deleteAll() {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        key = null
    }
}
