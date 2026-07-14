package pt.socialfood.mapper

import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.domain.model.Author

fun AuthorResponse.toAuthor(): Author =
    Author(
        id = this.id,
        name = this.name,
        imageUrl = this.imageUrl,
    )
