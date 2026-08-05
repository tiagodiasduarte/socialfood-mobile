package pt.socialfood.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.domain.repository.HomeRepository
import pt.socialfood.random.nextHomeSection
import kotlin.random.Random

@Suppress("LongParameterList")
class FakeHomeRepository(
    private val findAllResult: Result<List<HomeSection>> = Result.Success(emptyList()),
    homeSections: List<HomeSection> = emptyList(),
    private val findByIdResult: Result<HomeSection> = Result.Success(Random.nextHomeSection()),
    private val createResult: Result<HomeSection> = Result.Success(Random.nextHomeSection()),
    private val updateResult: Result<HomeSection> = Result.Success(Random.nextHomeSection()),
    private val deleteResult: Result<Boolean> = Result.Success(true),
    private val addItemResult: Result<HomeSection> = Result.Success(Random.nextHomeSection()),
    private val removeItemResult: Result<Boolean> = Result.Success(true),
) : HomeRepository {
    private val homeSectionsFlow = MutableStateFlow(homeSections)

    fun emitHomeSections(sections: List<HomeSection>) {
        homeSectionsFlow.value = sections
    }

    var lastFindByIdId: String? = null
        private set

    var createInvokeCount: Int = 0
        private set
    var lastCreateTitle: String? = null
        private set
    var lastCreateType: HomeSectionType? = null
        private set
    var lastCreatePosition: Int? = null
        private set

    var updateInvokeCount: Int = 0
        private set
    var lastUpdateId: String? = null
        private set
    var lastUpdateTitle: String? = null
        private set
    var lastUpdatePosition: Int? = null
        private set
    var lastUpdateIsActive: Boolean? = null
        private set
    var lastUpdateRestaurantIds: List<String>? = null
        private set
    var lastUpdateGuideIds: List<String>? = null
        private set

    var deleteInvokeCount: Int = 0
        private set
    var lastDeleteId: String? = null
        private set

    var addItemInvokeCount: Int = 0
        private set
    var lastAddItemSectionId: String? = null
        private set
    var lastAddItemItemId: String? = null
        private set
    var lastAddItemItemType: HomeItemType? = null
        private set
    var lastAddItemPosition: Int? = null
        private set

    var removeItemInvokeCount: Int = 0
        private set
    var lastRemoveItemSectionId: String? = null
        private set
    var lastRemoveItemItemId: String? = null
        private set

    override suspend fun findAll(): Result<List<HomeSection>> = findAllResult

    override fun observeHomeSections(): Flow<List<HomeSection>> = homeSectionsFlow

    override suspend fun findById(id: String): Result<HomeSection> {
        lastFindByIdId = id
        return findByIdResult
    }

    override suspend fun create(title: String, type: HomeSectionType, position: Int): Result<HomeSection> {
        createInvokeCount++
        lastCreateTitle = title
        lastCreateType = type
        lastCreatePosition = position
        return createResult
    }

    override suspend fun update(
        id: String,
        title: String,
        position: Int,
        isActive: Boolean,
        restaurantIds: List<String>,
        guideIds: List<String>,
    ): Result<HomeSection> {
        updateInvokeCount++
        lastUpdateId = id
        lastUpdateTitle = title
        lastUpdatePosition = position
        lastUpdateIsActive = isActive
        lastUpdateRestaurantIds = restaurantIds
        lastUpdateGuideIds = guideIds
        return updateResult
    }

    override suspend fun delete(id: String): Result<Boolean> {
        deleteInvokeCount++
        lastDeleteId = id
        return deleteResult
    }

    override suspend fun addItem(
        sectionId: String,
        itemId: String,
        itemType: HomeItemType,
        position: Int,
    ): Result<HomeSection> {
        addItemInvokeCount++
        lastAddItemSectionId = sectionId
        lastAddItemItemId = itemId
        lastAddItemItemType = itemType
        lastAddItemPosition = position
        return addItemResult
    }

    override suspend fun removeItem(sectionId: String, itemId: String): Result<Boolean> {
        removeItemInvokeCount++
        lastRemoveItemSectionId = sectionId
        lastRemoveItemItemId = itemId
        return removeItemResult
    }
}
