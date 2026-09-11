package pt.socialfood.domain.repository

interface LocalCacheRepository {

    suspend fun clearAll()
}
