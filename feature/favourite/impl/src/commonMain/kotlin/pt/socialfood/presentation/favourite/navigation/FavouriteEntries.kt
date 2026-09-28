package pt.socialfood.presentation.favourite.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import pt.socialfood.presentation.favourite.guide.FavouriteGuidesScreen
import pt.socialfood.presentation.favourite.restaurant.FavouriteRestaurantsScreen
import pt.socialfood.presentation.guide.navigation.GuideRoute
import pt.socialfood.presentation.navigation.Navigator
import pt.socialfood.presentation.navigation.slideHorizontalAnimationMetadata
import pt.socialfood.presentation.restaurant.navigation.RestaurantRoute

fun EntryProviderScope<NavKey>.favouriteEntries(navigator: Navigator) {
    entry<FavouriteRoute.FavouriteGuides>(metadata = slideHorizontalAnimationMetadata) {
        FavouriteGuidesScreen(
            onBackClick = navigator::goBack,
            onGuideClick = { guideId -> navigator.navigate(GuideRoute.GuideDetail(guideId)) },
        )
    }
    entry<FavouriteRoute.FavouriteRestaurants>(metadata = slideHorizontalAnimationMetadata) {
        FavouriteRestaurantsScreen(
            onBackClick = navigator::goBack,
            onRestaurantClick = { restaurantId ->
                navigator.navigate(RestaurantRoute.RestaurantDetail(restaurantId))
            },
        )
    }
}
