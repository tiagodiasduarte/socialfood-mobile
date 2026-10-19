package pt.socialfood.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.core.database.entity.GUIDES_TABLE
import pt.socialfood.core.database.entity.GuideEntity

@Dao
interface GuideDao {

    @Upsert
    suspend fun upsertAll(guides: List<GuideEntity>)

    @Query("DELETE FROM $GUIDES_TABLE WHERE scope = :scope")
    suspend fun deleteByScope(scope: String)

    @Query("DELETE FROM $GUIDES_TABLE")
    suspend fun deleteAll()

    @Query("SELECT * FROM $GUIDES_TABLE WHERE scope = :scope ORDER BY position ASC")
    fun pagingSource(scope: String): PagingSource<Int, GuideEntity>
}
