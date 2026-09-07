package pt.socialfood.fakes

import androidx.sqlite.SQLiteException
import pt.socialfood.data.local.dao.FavouriteGuideRemoteKeyDao
import pt.socialfood.data.local.entity.FavouriteGuideRemoteKeyEntity

class FakeFavouriteGuideRemoteKeyDao(private val shouldThrowOnWrite: Boolean = false) : FavouriteGuideRemoteKeyDao {

    private val keys = mutableMapOf<String, FavouriteGuideRemoteKeyEntity>()

    override suspend fun upsert(key: FavouriteGuideRemoteKeyEntity) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        keys[key.scope] = key
    }

    override suspend fun getByScope(scope: String): FavouriteGuideRemoteKeyEntity? = keys[scope]

    override suspend fun deleteByScope(scope: String) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        keys.remove(scope)
    }
}
