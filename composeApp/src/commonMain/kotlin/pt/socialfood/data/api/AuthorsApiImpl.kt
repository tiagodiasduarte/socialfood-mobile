package pt.socialfood.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.author.AuthorDetailResponse
import pt.socialfood.data.network.model.author.AuthorResponse

class AuthorsApiImpl(
    private val client: HttpClient,
) : AuthorsApi {
    override suspend fun findAuthors(page: Int, limit: Int, query: String?): PagedResponse<AuthorResponse> =
        client.get("authors") {
            parameter("page", page)
            parameter("limit", limit)
            if (!query.isNullOrBlank()) parameter("query", query)
        }.body()

    override suspend fun findAuthorById(id: String): AuthorDetailResponse =
        client.get("authors/$id").body()
}
