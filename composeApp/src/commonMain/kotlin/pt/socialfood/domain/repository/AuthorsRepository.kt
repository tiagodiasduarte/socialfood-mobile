package pt.socialfood.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.model.PagedAuthors

interface AuthorsRepository {
    suspend fun findAuthors(page: Int, limit: Int, query: String? = null): Result<PagedAuthors>
    suspend fun findAuthorById(id: String): Result<AuthorDetail>

    /**
     * Room-backed, refresh-on-fetch paging stream for the unscoped Authors list. Deliberately
     * returns `Flow<PagingData<Author>>` rather than `Result<PagedAuthors>` — the only method on
     * this repository that does — because Paging's error surface is
     * `LoadState`/`RemoteMediator.MediatorResult`, not a one-shot `Result`; see
     * `AuthorRemoteMediator` for the sync logic. Unlike `GuidesRepository.getGuidesPagingFlow`,
     * there is no scope/userId parameter — there is nothing to scope the Authors list by.
     */
    fun getAuthorsPagingFlow(): Flow<PagingData<Author>>
}
