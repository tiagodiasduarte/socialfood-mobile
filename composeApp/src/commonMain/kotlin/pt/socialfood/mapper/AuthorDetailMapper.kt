package pt.socialfood.mapper

import pt.socialfood.data.network.model.author.AuthorDetailResponse
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.mapper.toAuthorDetailGuide

fun AuthorDetailResponse.toAuthorDetail(): AuthorDetail =
    AuthorDetail(
        id = this.id,
        name = this.name,
        bio = this.bio,
        imageUrl = this.imageUrl,
        guidesCount = this.guidesCount,
        followersCount = this.followersCount,
        followingCount = this.followingCount,
        guides = this.guides.map { it.toAuthorDetailGuide() },
    )
