package pt.socialfood.core.testing.fakes

import androidx.sqlite.SQLiteException
import pt.socialfood.core.database.dao.RestaurantVisitStatusRemoteKeyDao
import pt.socialfood.core.database.entity.RestaurantVisitStatusRemoteKeyEntity

class FakeRestaurantVisitStatusRemoteKeyDao(private val shouldThrowOnWrite: Boolean = false) :
    RestaurantVisitStatusRemoteKeyDao {

    private val keys = mutableMapOf<String, RestaurantVisitStatusRemoteKeyEntity>()

    override suspend fun upsert(key: RestaurantVisitStatusRemoteKeyEntity) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        keys[key.scope] = key
    }

    override suspend fun getByScope(scope: String): RestaurantVisitStatusRemoteKeyEntity? = keys[scope]

    override suspend fun deleteByScope(scope: String) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        keys.remove(scope)
    }

    override suspend fun deleteAll() {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        keys.clear()
    }
}
