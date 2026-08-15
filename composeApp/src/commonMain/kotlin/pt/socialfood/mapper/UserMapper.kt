package pt.socialfood.mapper

import pt.socialfood.data.network.model.user.UserResponse
import pt.socialfood.domain.model.User
import pt.socialfood.domain.model.UserRole

fun UserResponse.toUser(): User = User(
    id = this.id,
    email = this.email,
    name = this.name,
    username = this.username,
    role = runCatching { UserRole.valueOf(this.role.uppercase()) }.getOrDefault(UserRole.USER),
    imageUrl = this.imageUrl,
    facebookUrl = this.facebookUrl,
    instagramUrl = this.instagramUrl,
    youtubeUrl = this.youtubeUrl,
    isVerified = this.isVerified,
)
