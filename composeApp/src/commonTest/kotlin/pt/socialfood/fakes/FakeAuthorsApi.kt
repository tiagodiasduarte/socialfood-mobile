package pt.socialfood.fakes

import pt.socialfood.data.AuthorsApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.author.AuthorDetailResponse
import pt.socialfood.data.network.model.author.AuthorResponse

class FakeAuthorsApi(private val shouldThrow: Boolean = false) : AuthorsApi {

    private val fakeAuthorResponse = AuthorResponse(
        id = "author-id",
        name = "Author Name",
        imageUrl = null,
    )

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

    override suspend fun findAuthors(page: Int, limit: Int, query: String?): PagedResponse<AuthorResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        return PagedResponse(
            items = listOf(fakeAuthorResponse),
            page = page,
            limit = limit,
            total = 25,
        )
    }

    override suspend fun findAuthorById(id: String): AuthorDetailResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeAuthorDetailResponse
    }
}
