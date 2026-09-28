package pt.socialfood.presentation.restaurant.navigation

import androidx.compose.runtime.MutableState
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.presentation.map.navigation.MapRoute
import pt.socialfood.presentation.navigation.Navigator
import pt.socialfood.presentation.navigation.defaultAnimationMetadata
import pt.socialfood.presentation.navigation.slideHorizontalAnimationMetadata
import pt.socialfood.presentation.restaurant.detail.RestaurantDetailScreen
import pt.socialfood.presentation.restaurant.search.SearchRestaurantsScreen
import pt.socialfood.presentation.restaurant.visited.RestaurantVisitedScreen
import pt.socialfood.presentation.restaurant.wishlist.RestaurantWishlistScreen

@Suppress("LongMethod")
fun EntryProviderScope<NavKey>.restaurantEntries(
    navigator: Navigator,
    onRestaurantAddedRef: MutableState<((Restaurant) -> Unit)?>,
) {
    entry<RestaurantRoute.AddRestaurants> { route ->
        SearchRestaurantsScreen(
            guideId = route.guideId,
            onBackClick = navigator::goBack,
            onRestaurantAdded = { restaurant ->
                onRestaurantAddedRef.value?.invoke(restaurant)
                navigator.goBack()
            },
        )
    }
    entry<RestaurantRoute.AddVisitedRestaurant> {
        SearchRestaurantsScreen(
            guideId = "",
            onBackClick = navigator::goBack,
            onRestaurantAdded = { restaurant ->
                onRestaurantAddedRef.value?.invoke(restaurant)
                navigator.goBack()
            },
        )
    }
    entry<RestaurantRoute.AddWishRestaurant> {
        SearchRestaurantsScreen(
            guideId = "",
            onBackClick = navigator::goBack,
            onRestaurantAdded = { restaurant ->
                onRestaurantAddedRef.value?.invoke(restaurant)
                navigator.goBack()
            },
        )
    }
    entry<RestaurantRoute.RestaurantDetail>(metadata = defaultAnimationMetadata) { route ->
        RestaurantDetailScreen(
            restaurantId = route.restaurantId,
            onBackClick = navigator::goBack,
            onViewMapClick = { restaurantId ->
                navigator.navigate(MapRoute.RestaurantMap(restaurantId))
            },
        )
    }
    entry<RestaurantRoute.VisitedRestaurants>(metadata = slideHorizontalAnimationMetadata) {
        RestaurantVisitedScreen(
            onBackClick = navigator::goBack,
            onRestaurantClick = { restaurantId ->
                navigator.navigate(RestaurantRoute.RestaurantDetail(restaurantId))
            },
            onAddClick = { onRestaurantAdded ->
                onRestaurantAddedRef.value = onRestaurantAdded
                navigator.navigate(RestaurantRoute.AddVisitedRestaurant)
            },
            onMapClick = { navigator.navigate(MapRoute.RestaurantsMap(VisitStatus.VISITED)) },
        )
    }
    entry<RestaurantRoute.WishRestaurants>(metadata = slideHorizontalAnimationMetadata) {
        RestaurantWishlistScreen(
            onBackClick = navigator::goBack,
            onRestaurantClick = { restaurantId ->
                navigator.navigate(RestaurantRoute.RestaurantDetail(restaurantId))
            },
            onAddClick = { onRestaurantAdded ->
                onRestaurantAddedRef.value = onRestaurantAdded
                navigator.navigate(RestaurantRoute.AddWishRestaurant)
            },
            onMapClick = { navigator.navigate(MapRoute.RestaurantsMap(VisitStatus.WISHLIST)) },
        )
    }
}
