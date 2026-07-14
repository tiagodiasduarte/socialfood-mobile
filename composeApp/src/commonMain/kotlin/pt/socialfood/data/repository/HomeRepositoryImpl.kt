package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.HomeApi
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.domain.repository.HomeRepository
import pt.socialfood.mapper.toHomeSection

class HomeRepositoryImpl(private val homeApi: HomeApi) : HomeRepository {

    override suspend fun findAll(): Result<List<HomeSection>> = try {
        Result.Success(homeApi.findAll().map { it.toHomeSection() })
    } catch (e: Exception) {
        Result.Error(e.toErrorEntity())
    }

    override suspend fun findById(id: String): Result<HomeSection> = try {
        Result.Success(homeApi.findById(id).toHomeSection())
    } catch (e: Exception) {
        Result.Error(e.toErrorEntity())
    }

    override suspend fun create(title: String, type: HomeSectionType, position: Int): Result<HomeSection> = try {
        Result.Success(homeApi.create(title, type.name, position).toHomeSection())
    } catch (e: Exception) {
        Result.Error(e.toErrorEntity())
    }

    override suspend fun update(
        id: String,
        title: String,
        position: Int,
        isActive: Boolean,
        restaurantIds: List<String>,
        guideIds: List<String>,
    ): Result<HomeSection> = try {
        Result.Success(homeApi.update(id, title, position, isActive, restaurantIds, guideIds).toHomeSection())
    } catch (e: Exception) {
        Result.Error(e.toErrorEntity())
    }

    override suspend fun delete(id: String): Result<Boolean> = try {
        homeApi.delete(id)
        Result.Success(true)
    } catch (e: Exception) {
        Result.Error(e.toErrorEntity())
    }

    override suspend fun addItem(sectionId: String, itemId: String, itemType: HomeItemType, position: Int): Result<HomeSection> = try {
        Result.Success(homeApi.addItem(sectionId, itemId, itemType.name, position).toHomeSection())
    } catch (e: Exception) {
        Result.Error(e.toErrorEntity())
    }

    override suspend fun removeItem(sectionId: String, itemId: String): Result<Boolean> = try {
        homeApi.removeItem(sectionId, itemId)
        Result.Success(true)
    } catch (e: Exception) {
        Result.Error(e.toErrorEntity())
    }
}