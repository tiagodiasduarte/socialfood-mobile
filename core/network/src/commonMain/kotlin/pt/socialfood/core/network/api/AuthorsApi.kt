package pt.socialfood.core.network.api

import pt.socialfood.core.network.model.PagedResponse
import pt.socialfood.core.network.model.author.AuthorDetailResponse
import pt.socialfood.core.network.model.author.AuthorResponse

interface AuthorsApi {
    suspend fun findAuthors(page: Int, limit: Int, query: String? = null): PagedResponse<AuthorResponse>

    suspend fun findAuthorById(id: String): AuthorDetailResponse
}
