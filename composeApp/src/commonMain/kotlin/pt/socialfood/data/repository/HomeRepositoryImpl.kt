package pt.socialfood.data.repository

import androidx.sqlite.SQLiteException
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import pt.socialfood.core.Result
import pt.socialfood.data.api.HomeApi
import pt.socialfood.data.local.dao.HomeDao
import pt.socialfood.data.network.extensions.toApiError
import pt.socialfood.data.paging.HomeCacheTransactionRunner
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.domain.repository.HomeRepository
import pt.socialfood.mapper.toHomeSection
import pt.socialfood.mapper.toHomeSectionEntity

class HomeRepositoryImpl(
    private val homeApi: HomeApi,
    private val homeDao: HomeDao,
    private val transactionRunner: HomeCacheTransactionRunner,
) : HomeRepository {
    override suspend fun findAll(): Result<List<HomeSection>> =
        try {
            val response = homeApi.findAll()
            transactionRunner.run {
                homeDao.deleteAll()
                homeDao.upsertAll(response.map { it.toHomeSectionEntity() })
            }
            Result.Success(response.map { it.toHomeSection() })
        } catch (e: IOException) {
            fallbackToCache(e)
        } catch (e: ResponseException) {
            fallbackToCache(e)
        } catch (e: SQLiteException) {
            fallbackToCache(e)
        }

    private suspend fun fallbackToCache(exception: Throwable): Result<List<HomeSection>> {
        val cached = homeDao.getAllActive()
        return if (cached.isNotEmpty()) {
            Result.Success(cached.map { it.toHomeSection() })
        } else {
            Result.Failure(exception.toApiError())
        }
    }

    override fun observeHomeSections(): Flow<List<HomeSection>> =
        homeDao.observeActive().map { entities -> entities.map { it.toHomeSection() } }

    override suspend fun findById(id: String): Result<HomeSection> =
        safeApiCall { homeApi.findById(id).toHomeSection() }

    override suspend fun create(
        title: String,
        type: HomeSectionType,
        position: Int,
    ): Result<HomeSection> =
        safeApiCall { homeApi.create(title, type.name, position).toHomeSection() }

    override suspend fun update(
        id: String,
        title: String,
        position: Int,
        isActive: Boolean,
        restaurantIds: List<String>,
        guideIds: List<String>,
    ): Result<HomeSection> =
        safeApiCall { homeApi.update(id, title, position, isActive, restaurantIds, guideIds).toHomeSection() }

    override suspend fun delete(id: String): Result<Boolean> =
        safeApiCall {
            homeApi.delete(id)
            true
        }

    override suspend fun addItem(
        sectionId: String,
        itemId: String,
        itemType: HomeItemType,
        position: Int,
    ): Result<HomeSection> =
        safeApiCall { homeApi.addItem(sectionId, itemId, itemType.name, position).toHomeSection() }

    override suspend fun removeItem(
        sectionId: String,
        itemId: String,
    ): Result<Boolean> =
        safeApiCall {
            homeApi.removeItem(sectionId, itemId)
            true
        }
}
