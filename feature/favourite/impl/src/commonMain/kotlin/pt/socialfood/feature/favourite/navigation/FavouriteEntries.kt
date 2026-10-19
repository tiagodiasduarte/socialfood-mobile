package pt.socialfood.feature.favourite.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import pt.socialfood.core.navigation.Navigator
import pt.socialfood.core.navigation.slideHorizontalAnimationMetadata
import pt.socialfood.feature.favourite.guide.FavouriteGuidesScreen
import pt.socialfood.feature.favourite.restaurant.FavouriteRestaurantsScreen
import pt.socialfood.feature.guide.navigation.GuideRoute
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
