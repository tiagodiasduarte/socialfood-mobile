package pt.socialfood.mapper

import pt.socialfood.core.model.Author
import pt.socialfood.data.local.entity.AuthorEntity
import pt.socialfood.data.network.model.author.AuthorResponse

fun AuthorResponse.toAuthor(): Author = Author(
    id = this.id,
    name = this.name,
    username = this.username,
    imageUrl = this.imageUrl,
    isPublic = this.isPublic,
)

fun Author.toAuthorEntity(position: Int): AuthorEntity = AuthorEntity(
    id = this.id,
    name = this.name,
    username = this.username,
    imageUrl = this.imageUrl,
    isPublic = this.isPublic,
    position = position,
)

fun AuthorEntity.toAuthor(): Author = Author(
    id = this.id,
    name = this.name,
    username = this.username,
    imageUrl = this.imageUrl,
    isPublic = this.isPublic,
)
