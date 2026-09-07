package pt.socialfood.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import pt.socialfood.data.local.entity.FAVOURITE_GUIDES_TABLE
import pt.socialfood.data.local.entity.FavouriteGuideEntity

@Dao
interface FavouriteDao {

    @Upsert
    suspend fun upsert(favourite: FavouriteGuideEntity)

    @Query("SELECT guideId FROM $FAVOURITE_GUIDES_TABLE")
    fun observeAllIds(): Flow<List<String>>

    @Upsert
    suspend fun upsertAll(favourites: List<FavouriteGuideEntity>)

    @Query("DELETE FROM $FAVOURITE_GUIDES_TABLE WHERE guideId = :guideId")
    suspend fun deleteByGuideId(guideId: String)

    @Query("DELETE FROM $FAVOURITE_GUIDES_TABLE WHERE guideId IN (:guideIds)")
    suspend fun deleteByGuideIds(guideIds: List<String>)

    @Query("DELETE FROM $FAVOURITE_GUIDES_TABLE")
    suspend fun deleteAll()

    @Query(
        "SELECT * FROM $FAVOURITE_GUIDES_TABLE WHERE syncState != 'PENDING_REMOVE' ORDER BY position ASC",
    )
    fun pagingSource(): PagingSource<Int, FavouriteGuideEntity>

    @Query("SELECT * FROM $FAVOURITE_GUIDES_TABLE WHERE guideId = :guideId LIMIT 1")
    suspend fun getByGuideId(guideId: String): FavouriteGuideEntity?

    @Query("SELECT * FROM $FAVOURITE_GUIDES_TABLE WHERE syncState != 'SYNCED'")
    suspend fun getPending(): List<FavouriteGuideEntity>

    @Query("UPDATE $FAVOURITE_GUIDES_TABLE SET syncState = :syncState WHERE guideId = :guideId")
    suspend fun updateSyncState(guideId: String, syncState: String)
}
