package pt.socialfood.presentation.map.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.presentation.map.restaurant.MapRestaurantScreen
import pt.socialfood.presentation.map.restaurant.visitstatus.MapRestaurantVisitStatusScreen
import pt.socialfood.presentation.navigation.Navigator
import pt.socialfood.presentation.navigation.slideUpAnimationMetadata
import pt.socialfood.presentation.restaurant.navigation.RestaurantRoute
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.visited_restaurants_title
import socialfood.composeapp.generated.resources.wish_restaurants_title

fun EntryProviderScope<NavKey>.mapEntries(navigator: Navigator) {
    entry<MapRoute.RestaurantMap>(metadata = slideUpAnimationMetadata) { route ->
        MapRestaurantScreen(
            restaurantId = route.restaurantId,
            onBackClick = navigator::goBack,
        )
    }
    entry<MapRoute.RestaurantsMap>(metadata = slideUpAnimationMetadata) { route ->
        MapRestaurantVisitStatusScreen(
            status = route.status,
            title = when (route.status) {
                VisitStatus.WISHLIST -> stringResource(Res.string.wish_restaurants_title)
                VisitStatus.VISITED -> stringResource(Res.string.visited_restaurants_title)
            },
            onBackClick = navigator::goBack,
            onRestaurantClick = { restaurantId ->
                navigator.navigate(RestaurantRoute.RestaurantDetail(restaurantId))
            },
        )
    }
}
