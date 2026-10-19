package pt.socialfood.feature.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import pt.socialfood.core.navigation.Navigator
import pt.socialfood.core.navigation.defaultAnimationMetadata
import pt.socialfood.feature.guide.navigation.GuideRoute
import pt.socialfood.feature.home.HomeScreen
import pt.socialfood.feature.restaurant.navigation.RestaurantRoute
import pt.socialfood.feature.search.navigation.SearchRoute

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
