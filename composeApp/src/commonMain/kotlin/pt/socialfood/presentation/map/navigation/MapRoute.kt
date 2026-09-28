package pt.socialfood.presentation.map.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import pt.socialfood.domain.model.VisitStatus

@Serializable
sealed interface MapRoute : NavKey {

    @Serializable
    data class RestaurantMap(val restaurantId: String) : MapRoute

    @Serializable
    data class RestaurantsMap(val status: VisitStatus) : MapRoute
}
