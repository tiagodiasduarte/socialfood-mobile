package pt.socialfood.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import pt.socialfood.data.local.entity.FavouriteGuideEntity

@Dao
interface FavouriteDao {

    @Upsert
    suspend fun upsert(favourite: FavouriteGuideEntity)

    @Query("SELECT guideId FROM favourite_guides")
    fun observeAllIds(): Flow<List<String>>

    @Upsert
    suspend fun upsertAll(favourites: List<FavouriteGuideEntity>)

    @Query("DELETE FROM favourite_guides WHERE guideId = :guideId")
    suspend fun deleteByGuideId(guideId: String)

    @Query("DELETE FROM favourite_guides WHERE guideId IN (:guideIds)")
    suspend fun deleteByGuideIds(guideIds: List<String>)

    @Query("SELECT * FROM favourite_guides ORDER BY favouritedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaged(limit: Int, offset: Int): List<FavouriteGuideEntity>

    @Query("SELECT COUNT(*) FROM favourite_guides")
    suspend fun countAll(): Int

    @Query("SELECT * FROM favourite_guides WHERE guideId = :guideId LIMIT 1")
    suspend fun getByGuideId(guideId: String): FavouriteGuideEntity?

    @Query("SELECT * FROM favourite_guides WHERE syncState != 'SYNCED'")
    suspend fun getPending(): List<FavouriteGuideEntity>

    @Query("UPDATE favourite_guides SET syncState = :syncState WHERE guideId = :guideId")
    suspend fun updateSyncState(guideId: String, syncState: String)
}
