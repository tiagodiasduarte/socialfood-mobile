package pt.socialfood.presentation.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import pt.socialfood.presentation.guide.navigation.GuideRoute
import pt.socialfood.presentation.home.HomeScreen
import pt.socialfood.presentation.navigation.Navigator
import pt.socialfood.presentation.navigation.defaultAnimationMetadata
import pt.socialfood.presentation.restaurant.navigation.RestaurantRoute
import pt.socialfood.presentation.search.navigation.SearchRoute

fun EntryProviderScope<NavKey>.homeEntries(navigator: Navigator, onOpenDrawer: () -> Unit) {
    entry<HomeRoute.Home>(metadata = defaultAnimationMetadata) {
        HomeScreen(
            onGuideClick = { guideId -> navigator.navigate(GuideRoute.GuideDetail(guideId)) },
            onRestaurantClick = { restaurantId ->
                navigator.navigate(RestaurantRoute.RestaurantDetail(restaurantId))
            },
            onProfileClick = onOpenDrawer,
            onSearchClick = { navigator.navigate(SearchRoute.Search) },
        )
    }
}
