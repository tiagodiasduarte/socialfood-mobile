package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.model.PagedAuthors

interface AuthorsRepository {
    suspend fun findAuthors(page: Int, limit: Int, query: String? = null): Result<PagedAuthors>
    suspend fun findAuthorById(id: String): Result<AuthorDetail>
}
