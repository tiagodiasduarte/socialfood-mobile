package pt.socialfood.core.domain.repository

interface LocalCacheRepository {

    suspend fun clearAll()
}
