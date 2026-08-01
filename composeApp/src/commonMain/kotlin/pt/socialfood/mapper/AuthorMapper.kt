package pt.socialfood.mapper

import pt.socialfood.data.local.entity.AuthorEntity
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.domain.model.Author

fun AuthorResponse.toAuthor(): Author =
    Author(
        id = this.id,
        name = this.name,
        username = this.username,
        imageUrl = this.imageUrl,
    )

fun Author.toAuthorEntity(position: Int): AuthorEntity =
    AuthorEntity(
        id = this.id,
        name = this.name,
        username = this.username,
        imageUrl = this.imageUrl,
        position = position,
    )

fun AuthorEntity.toAuthor(): Author =
    Author(
        id = this.id,
        name = this.name,
        username = this.username,
        imageUrl = this.imageUrl,
    )
