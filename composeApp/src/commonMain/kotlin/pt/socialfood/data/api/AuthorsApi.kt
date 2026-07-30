package pt.socialfood.data.api

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.author.AuthorDetailResponse
import pt.socialfood.data.network.model.author.AuthorResponse

interface AuthorsApi {
    suspend fun findAuthors(page: Int, limit: Int, query: String? = null): PagedResponse<AuthorResponse>

    suspend fun findAuthorById(id: String): AuthorDetailResponse
}
