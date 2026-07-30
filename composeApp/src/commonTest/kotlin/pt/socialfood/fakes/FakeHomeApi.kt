package pt.socialfood.fakes

import pt.socialfood.data.api.HomeApi
import pt.socialfood.data.network.model.home.HomeSectionResponse

class FakeHomeApi(private val shouldThrow: Boolean = false) : HomeApi {

    private val fakeHomeSectionResponse = HomeSectionResponse(
        id = "section-id",
        title = "Section Title",
        type = "RESTAURANT_LIST",
        position = 0,
        isActive = true,
        items = emptyList(),
    )

    override suspend fun findAll(): List<HomeSectionResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        return listOf(fakeHomeSectionResponse)
    }

    override suspend fun findById(id: String): HomeSectionResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeHomeSectionResponse
    }

    override suspend fun create(title: String, type: String, position: Int): HomeSectionResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeHomeSectionResponse
    }

    override suspend fun update(
        id: String,
        title: String,
        position: Int,
        isActive: Boolean,
        restaurantIds: List<String>,
        guideIds: List<String>,
    ): HomeSectionResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeHomeSectionResponse
    }

    override suspend fun delete(id: String) {
        if (shouldThrow) throw RuntimeException("test error")
    }

    override suspend fun addItem(sectionId: String, itemId: String, itemType: String, position: Int): HomeSectionResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeHomeSectionResponse
    }

    override suspend fun removeItem(sectionId: String, itemId: String) {
        if (shouldThrow) throw RuntimeException("test error")
    }
}
