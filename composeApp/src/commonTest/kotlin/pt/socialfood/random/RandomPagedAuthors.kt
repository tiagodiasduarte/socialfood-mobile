package pt.socialfood.random

import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.PagedAuthors

fun randomPagedAuthors(
    authors: List<Author> = randomList { randomAuthor() },
    page: Int = randomInt(1, 10),
    hasMore: Boolean = randomBoolean(),
) = PagedAuthors(authors = authors, page = page, hasMore = hasMore)
