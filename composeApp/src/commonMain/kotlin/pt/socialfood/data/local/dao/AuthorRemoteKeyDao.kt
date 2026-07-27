package pt.socialfood.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.data.local.entity.AuthorRemoteKeyEntity

@Dao
interface AuthorRemoteKeyDao {

    @Upsert
    suspend fun upsert(key: AuthorRemoteKeyEntity)

    @Query("SELECT * FROM author_remote_key WHERE id = 0")
    suspend fun get(): AuthorRemoteKeyEntity?

    @Query("DELETE FROM author_remote_key")
    suspend fun deleteAll()
}
