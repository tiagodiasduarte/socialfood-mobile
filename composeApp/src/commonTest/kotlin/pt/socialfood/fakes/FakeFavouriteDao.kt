package pt.socialfood.fakes

import pt.socialfood.data.local.dao.FavouriteDao
import pt.socialfood.data.local.entity.FavouriteGuideEntity

class FakeFavouriteDao(private val shouldThrowOnWrite: Boolean = false) : FavouriteDao {

    private val entities = LinkedHashMap<String, FavouriteGuideEntity>()

    override suspend fun upsert(favourite: FavouriteGuideEntity) {
        if (shouldThrowOnWrite) throw RuntimeException("test error")
        entities[favourite.guideId] = favourite
    }

    override suspend fun upsertAll(favourites: List<FavouriteGuideEntity>) {
        if (shouldThrowOnWrite) throw RuntimeException("test error")
        favourites.forEach { entities[it.guideId] = it }
    }

    override suspend fun deleteByGuideId(guideId: String) {
        if (shouldThrowOnWrite) throw RuntimeException("test error")
        entities.remove(guideId)
    }

    override suspend fun deleteByGuideIds(guideIds: List<String>) {
        if (shouldThrowOnWrite) throw RuntimeException("test error")
        guideIds.forEach { entities.remove(it) }
    }

    override suspend fun getPaged(limit: Int, offset: Int): List<FavouriteGuideEntity> =
        entities.values
            .sortedByDescending { it.favouritedAt }
            .drop(offset)
            .take(limit)

    override suspend fun countAll(): Int = entities.size

    override suspend fun getByGuideId(guideId: String): FavouriteGuideEntity? = entities[guideId]

    override suspend fun getPending(): List<FavouriteGuideEntity> =
        entities.values.filter { it.syncState != "SYNCED" }

    override suspend fun updateSyncState(guideId: String, syncState: String) {
        entities[guideId]?.let { entities[guideId] = it.copy(syncState = syncState) }
    }
}
