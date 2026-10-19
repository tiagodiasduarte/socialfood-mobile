package pt.socialfood.core.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.common.Result
import pt.socialfood.core.model.HomeItemType
import pt.socialfood.core.model.HomeSection
import pt.socialfood.core.model.HomeSectionType

interface HomeRepository {
    suspend fun findAll(): Result<List<HomeSection>>

    fun observeHomeSections(): Flow<List<HomeSection>>

    suspend fun findById(id: String): Result<HomeSection>

    suspend fun create(title: String, type: HomeSectionType, position: Int): Result<HomeSection>

    suspend fun update(
        id: String,
        title: String,
        position: Int,
        isActive: Boolean,
        restaurantIds: List<String> = emptyList(),
        guideIds: List<String> = emptyList(),
    ): Result<HomeSection>

    suspend fun delete(id: String): Result<Boolean>

    suspend fun addItem(sectionId: String, itemId: String, itemType: HomeItemType, position: Int): Result<HomeSection>

    suspend fun removeItem(sectionId: String, itemId: String): Result<Boolean>
}
