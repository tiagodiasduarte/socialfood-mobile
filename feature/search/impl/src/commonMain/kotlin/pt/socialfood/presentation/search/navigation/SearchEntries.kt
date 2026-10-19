package pt.socialfood.presentation.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import pt.socialfood.core.navigation.Navigator
import pt.socialfood.core.navigation.defaultAnimationMetadata
import pt.socialfood.feature.author.navigation.AuthorRoute
import pt.socialfood.feature.guide.navigation.GuideRoute
import pt.socialfood.presentation.restaurant.navigation.RestaurantRoute
import pt.socialfood.presentation.search.SearchScreen

fun EntryProviderScope<NavKey>.searchEntries(navigator: Navigator) {
    entry<SearchRoute.Search>(metadata = defaultAnimationMetadata) {
        SearchScreen(
            onAuthorClick = { authorId -> navigator.navigate(AuthorRoute.AuthorDetail(authorId)) },
            onGuideClick = { guideId -> navigator.navigate(GuideRoute.GuideDetail(guideId)) },
            onRestaurantClick = { restaurantId ->
                navigator.navigate(RestaurantRoute.RestaurantDetail(restaurantId))
            },
        )
    }
}
