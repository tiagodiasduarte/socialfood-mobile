package pt.socialfood.presentation.author.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthorRoute : NavKey {

    @Serializable
    data object Authors : AuthorRoute

    @Serializable
    data class AuthorDetail(val authorId: String) : AuthorRoute

    @Serializable
    data class Profile(val authorId: String) : AuthorRoute
}
