package pt.socialfood.fakes

import pt.socialfood.data.AuthorsApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.author.AuthorDetailResponse
import pt.socialfood.data.network.model.author.AuthorResponse

class FakeAuthorsApi(
    private val shouldThrow: Boolean = false,
    private val items: List<AuthorResponse> = listOf(
        AuthorResponse(id = "author-id", name = "Author Name", imageUrl = null),
    ),
    private val total: Int = 25,
) : AuthorsApi {

    private val fakeAuthorDetailResponse = AuthorDetailResponse(
        id = "author-id",
        name = "Author Name",
        imageUrl = null,
        guidesCount = 3,
        followersCount = 10,
        followingCount = 5,
        isFollowing = false,
        guides = emptyList(),
    )

    var findAuthorsCallCount: Int = 0
        private set
    var lastFindAuthorsPage: Int? = null
        private set

    override suspend fun findAuthors(page: Int, limit: Int, query: String?): PagedResponse<AuthorResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        findAuthorsCallCount++
        lastFindAuthorsPage = page
        return PagedResponse(
            items = items,
            page = page,
            limit = limit,
            total = total,
        )
    }

    override suspend fun findAuthorById(id: String): AuthorDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeAuthorDetailResponse
    }
}
