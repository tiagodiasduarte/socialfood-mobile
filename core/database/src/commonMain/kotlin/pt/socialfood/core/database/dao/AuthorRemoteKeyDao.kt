package pt.socialfood.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pt.socialfood.core.database.entity.AUTHOR_REMOTE_KEY_TABLE
import pt.socialfood.core.database.entity.AuthorRemoteKeyEntity

@Dao
interface AuthorRemoteKeyDao {

    @Upsert
    suspend fun upsert(key: AuthorRemoteKeyEntity)

    @Query("SELECT * FROM $AUTHOR_REMOTE_KEY_TABLE WHERE id = 0")
    suspend fun get(): AuthorRemoteKeyEntity?

    @Query("DELETE FROM $AUTHOR_REMOTE_KEY_TABLE")
    suspend fun deleteAll()
}
