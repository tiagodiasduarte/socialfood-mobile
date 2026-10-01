package pt.socialfood.presentation.guide.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import pt.socialfood.presentation.author.navigation.AuthorRoute
import pt.socialfood.presentation.guide.GuidesScreen
import pt.socialfood.presentation.guide.create.CreateGuideScreen
import pt.socialfood.presentation.guide.detail.GuideDetailScreen
import pt.socialfood.presentation.guide.edit.EditGuideScreen
import pt.socialfood.presentation.guide.map.GuideMapScreen
import pt.socialfood.presentation.navigation.Navigator
import pt.socialfood.presentation.navigation.defaultAnimationMetadata
import pt.socialfood.presentation.navigation.slideUpAnimationMetadata
import pt.socialfood.presentation.restaurant.navigation.RestaurantRoute

fun EntryProviderScope<NavKey>.guideEntries(navigator: Navigator, onOpenDrawer: () -> Unit) {
    entry<GuideRoute.CreateGuide> {
        CreateGuideScreen(
            onBackClick = navigator::goBack,
            onGuideCreated = { guideId ->
                navigator.goBack()
                navigator.navigate(GuideRoute.EditGuide(guideId, initialTab = 1))
            },
        )
    }
    entry<GuideRoute.EditGuide> { route ->
        EditGuideScreen(
            guideId = route.guideId,
            onBackClick = navigator::goBack,
            onGuideDeleted = navigator::popToRoot,
            initialTab = route.initialTab,
            onAddRestaurantsClick = { requestKey ->
                navigator.navigate(RestaurantRoute.PickRestaurant(requestKey))
            },
        )
    }
    entry<GuideRoute.GuideDetail>(metadata = defaultAnimationMetadata) { route ->
        GuideDetailScreen(
            guideId = route.guideId,
            onBackClick = navigator::goBack,
            onEditClick = { guideId -> navigator.navigate(GuideRoute.EditGuide(guideId)) },
            onRestaurantClick = { restaurantId ->
                navigator.navigate(RestaurantRoute.RestaurantDetail(restaurantId))
            },
            onAuthorClick = { authorId -> navigator.navigate(AuthorRoute.AuthorDetail(authorId)) },
            onViewMapClick = { guideId, guideName, restaurantsCount ->
                navigator.navigate(GuideRoute.GuideMap(guideId, guideName, restaurantsCount))
            },
        )
    }
    entry<GuideRoute.GuideMap>(metadata = slideUpAnimationMetadata) { route ->
        GuideMapScreen(
            guideId = route.guideId,
            guideName = route.guideName,
            restaurantsCount = route.restaurantsCount,
            onBackClick = navigator::goBack,
            onRestaurantClick = { restaurantId ->
                navigator.navigate(RestaurantRoute.RestaurantDetail(restaurantId))
            },
        )
    }
    entry<GuideRoute.Guides>(metadata = defaultAnimationMetadata) {
        GuidesScreen(
            onGuideClick = { guideId -> navigator.navigate(GuideRoute.GuideDetail(guideId)) },
            onAddClick = { navigator.navigate(GuideRoute.CreateGuide) },
            onProfileClick = onOpenDrawer,
            onGuideJoined = { guideId -> navigator.navigate(GuideRoute.GuideDetail(guideId)) },
        )
    }
}
