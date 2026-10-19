package pt.socialfood.core.data.mapper

import pt.socialfood.core.model.AuthorDetail
import pt.socialfood.core.network.model.author.AuthorDetailResponse

fun AuthorDetailResponse.toAuthorDetail(): AuthorDetail = AuthorDetail(
    id = this.id,
    name = this.name,
    username = this.username,
    imageUrl = this.imageUrl,
    guidesCount = this.guidesCount,
    followersCount = this.followersCount,
    followingCount = this.followingCount,
    facebookUrl = this.facebookUrl,
    instagramUrl = this.instagramUrl,
    youtubeUrl = this.youtubeUrl,
    guides = this.guides.map { it.toAuthorDetailGuide() },
)
