package pt.socialfood.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val username: String? = null,
    val imageUrl: String? = null,
    val role: UserRole = UserRole.USER,
    val address: String? = null,
    val phoneNumber: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val bio: String? = null,
    val city: String? = null,
    val country: String? = null,
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val youtubeUrl: String? = null,
)

enum class UserRole { USER, ADMIN }