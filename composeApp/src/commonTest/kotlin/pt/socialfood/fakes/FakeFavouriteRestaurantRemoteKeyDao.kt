package pt.socialfood.fakes

import androidx.sqlite.SQLiteException
import pt.socialfood.data.local.dao.FavouriteRestaurantRemoteKeyDao
import pt.socialfood.data.local.entity.FavouriteRestaurantRemoteKeyEntity

class FakeFavouriteRestaurantRemoteKeyDao(private val shouldThrowOnWrite: Boolean = false) :
    FavouriteRestaurantRemoteKeyDao {

    private val keys = mutableMapOf<String, FavouriteRestaurantRemoteKeyEntity>()

    override suspend fun upsert(key: FavouriteRestaurantRemoteKeyEntity) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        keys[key.scope] = key
    }

    override suspend fun getByScope(scope: String): FavouriteRestaurantRemoteKeyEntity? = keys[scope]

    override suspend fun deleteByScope(scope: String) {
        if (shouldThrowOnWrite) throw SQLiteException("test error")
        keys.remove(scope)
    }
}
