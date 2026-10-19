package pt.socialfood.core.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.common.Result
import pt.socialfood.core.model.Author
import pt.socialfood.core.model.AuthorDetail
import pt.socialfood.core.model.PagedAuthors

interface AuthorsRepository {
    suspend fun findAuthors(page: Int, limit: Int, query: String? = null): Result<PagedAuthors>
    suspend fun findAuthorById(id: String): Result<AuthorDetail>

    fun getAuthorsPagingFlow(): Flow<PagingData<Author>>
}
