package pt.socialfood.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.database.entity.HOME_SECTIONS_TABLE
import pt.socialfood.core.database.entity.HomeSectionEntity

@Dao
interface HomeDao {

    @Upsert
    suspend fun upsertAll(sections: List<HomeSectionEntity>)

    @Query("DELETE FROM $HOME_SECTIONS_TABLE")
    suspend fun deleteAll()

    @Query("SELECT * FROM $HOME_SECTIONS_TABLE WHERE isActive = 1 ORDER BY position ASC")
    fun observeActive(): Flow<List<HomeSectionEntity>>

    @Query("SELECT * FROM $HOME_SECTIONS_TABLE WHERE isActive = 1 ORDER BY position ASC")
    suspend fun getAllActive(): List<HomeSectionEntity>
}
