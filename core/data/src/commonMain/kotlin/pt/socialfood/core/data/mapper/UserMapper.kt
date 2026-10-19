package pt.socialfood.core.data.mapper

import pt.socialfood.core.model.User
import pt.socialfood.core.model.UserRole
import pt.socialfood.core.network.model.user.UserResponse

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
    isAuthor = this.isAuthor,
)
