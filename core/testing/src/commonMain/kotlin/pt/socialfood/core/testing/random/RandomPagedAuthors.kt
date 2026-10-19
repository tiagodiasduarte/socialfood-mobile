package pt.socialfood.core.testing.random

import pt.socialfood.core.model.Author
import pt.socialfood.core.model.PagedAuthors
import kotlin.random.Random

fun Random.nextPagedAuthors(
    authors: List<Author> = nextList { nextAuthor() },
    page: Int = nextInt(1, 10),
    hasMore: Boolean = nextBoolean(),
) = PagedAuthors(authors = authors, page = page, hasMore = hasMore)
