package pt.socialfood.random

import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.PagedAuthors
import kotlin.random.Random

fun Random.nextPagedAuthors(
    authors: List<Author> = nextList { nextAuthor() },
    page: Int = nextInt(1, 10),
    hasMore: Boolean = nextBoolean(),
) = PagedAuthors(authors = authors, page = page, hasMore = hasMore)
