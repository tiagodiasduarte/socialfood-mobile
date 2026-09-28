package pt.socialfood.data.api

import pt.socialfood.data.network.model.home.HomeSectionResponse

interface HomeApi {

    suspend fun findAll(): List<HomeSectionResponse>

    suspend fun findById(id: String): HomeSectionResponse

    suspend fun create(title: String, type: String, position: Int): HomeSectionResponse

    suspend fun update(
        id: String,
        title: String,
        position: Int,
        isActive: Boolean,
        restaurantIds: List<String> = emptyList(),
        guideIds: List<String> = emptyList(),
    ): HomeSectionResponse

    suspend fun delete(id: String)

    suspend fun addItem(sectionId: String, itemId: String, itemType: String, position: Int): HomeSectionResponse

    suspend fun removeItem(sectionId: String, itemId: String)
}
