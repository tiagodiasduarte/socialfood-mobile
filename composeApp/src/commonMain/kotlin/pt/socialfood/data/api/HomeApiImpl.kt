package pt.socialfood.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pt.socialfood.data.network.model.home.AddHomeSectionItemRequest
import pt.socialfood.data.network.model.home.CreateHomeSectionRequest
import pt.socialfood.data.network.model.home.HomeSectionResponse
import pt.socialfood.data.network.model.home.UpdateHomeSectionRequest

class HomeApiImpl(
    private val client: HttpClient,
) : HomeApi {
    override suspend fun findAll(): List<HomeSectionResponse> = client.get("home/sections").body()

    override suspend fun findById(id: String): HomeSectionResponse = client.get("home/sections/$id").body()

    override suspend fun create(
        title: String,
        type: String,
        position: Int,
    ): HomeSectionResponse =
        client
            .post("home/sections") {
                contentType(ContentType.Application.Json)
                setBody(CreateHomeSectionRequest(title = title, type = type, position = position))
            }.body()

    override suspend fun update(
        id: String,
        title: String,
        position: Int,
        isActive: Boolean,
        restaurantIds: List<String>,
        guideIds: List<String>,
    ): HomeSectionResponse =
        client
            .put("home/sections/$id") {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateHomeSectionRequest(
                        title = title,
                        position = position,
                        isActive = isActive,
                        restaurantIds = restaurantIds,
                        guideIds = guideIds,
                    ),
                )
            }.body()

    override suspend fun delete(id: String) {
        client.delete("home/sections/$id")
    }

    override suspend fun addItem(
        sectionId: String,
        itemId: String,
        itemType: String,
        position: Int,
    ): HomeSectionResponse =
        client
            .post("home/sections/$sectionId/items") {
                contentType(ContentType.Application.Json)
                setBody(AddHomeSectionItemRequest(itemId = itemId, itemType = itemType, position = position))
            }.body()

    override suspend fun removeItem(
        sectionId: String,
        itemId: String,
    ) {
        client.delete("home/sections/$sectionId/items/$itemId")
    }
}
