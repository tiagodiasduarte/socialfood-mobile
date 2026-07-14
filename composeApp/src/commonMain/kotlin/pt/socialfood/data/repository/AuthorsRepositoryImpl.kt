package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.AuthorsApi
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.model.PagedAuthors
import pt.socialfood.domain.repository.AuthorsRepository
import pt.socialfood.mapper.toAuthor
import pt.socialfood.mapper.toAuthorDetail

class AuthorsRepositoryImpl(
    private val authorsApi: AuthorsApi,
) : AuthorsRepository {
    override suspend fun findAuthors(page: Int, limit: Int, query: String?): Result<PagedAuthors> {
        return try {
            val response = authorsApi.findAuthors(page = page, limit = limit, query = query)
            val hasMore = response.page * response.limit < response.total
            Result.Success(
                PagedAuthors(
                    authors = response.items.map { it.toAuthor() },
                    page = response.page,
                    hasMore = hasMore,
                )
            )
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun findAuthorById(id: String): Result<AuthorDetail> {
        return try {
            val response = authorsApi.findAuthorById(id)
            Result.Success(response.toAuthorDetail())
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }
}
