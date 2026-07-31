package pt.socialfood.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val imageUrl: String? = null,
    val role: UserRole = UserRole.USER,
    val address: String? = null,
    val city: String? = null,
    val country: String? = null,
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val youtubeUrl: String? = null,
    val isVerified: Boolean = true,
)

enum class UserRole { USER, ADMIN }
