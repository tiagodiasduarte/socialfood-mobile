package pt.socialfood.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.data.local.entity.AUTHORS_TABLE
import pt.socialfood.data.local.entity.AuthorEntity

@Dao
interface AuthorDao {

    @Upsert
    suspend fun upsertAll(authors: List<AuthorEntity>)

    @Query("DELETE FROM $AUTHORS_TABLE")
    suspend fun deleteAll()

    @Query("SELECT * FROM $AUTHORS_TABLE ORDER BY position ASC")
    fun pagingSource(): PagingSource<Int, AuthorEntity>
}
