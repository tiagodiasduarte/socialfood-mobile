package pt.socialfood.fakes

import androidx.sqlite.SQLiteException
import pt.socialfood.data.local.dao.GuideRemoteKeyDao
import pt.socialfood.data.local.entity.GuideRemoteKeyEntity

class FakeGuideRemoteKeyDao(private val shouldThrowOnWrite: Boolean = false) : GuideRemoteKeyDao {

    private val keys = mutableMapOf<String, GuideRemoteKeyEntity>()

    override suspend fun upsert(key: GuideRemoteKeyEntity) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        keys[key.scope] = key
    }

    override suspend fun getByScope(scope: String): GuideRemoteKeyEntity? = keys[scope]

    override suspend fun deleteByScope(scope: String) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        keys.remove(scope)
    }
}
