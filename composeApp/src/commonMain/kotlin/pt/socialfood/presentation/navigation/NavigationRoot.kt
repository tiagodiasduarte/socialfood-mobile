package pt.socialfood.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.author.detail.AuthorDetailScreen
import pt.socialfood.presentation.author.list.AuthorsScreen
import pt.socialfood.presentation.favourite.guide.FavouriteGuidesScreen
import pt.socialfood.presentation.favourite.restaurant.FavouriteRestaurantsScreen
import pt.socialfood.presentation.guide.create.CreateGuideScreen
import pt.socialfood.presentation.guide.detail.GuideDetailScreen
import pt.socialfood.presentation.guide.edit.EditGuideScreen
import pt.socialfood.presentation.guide.list.GuidesScreen
import pt.socialfood.presentation.home.HomeScreen
import pt.socialfood.presentation.profile.ProfileScreen
import pt.socialfood.presentation.profile.edit.EditProfileScreen
import pt.socialfood.presentation.restaurant.detail.RestaurantDetailScreen
import pt.socialfood.presentation.restaurant.search.SearchRestaurantsScreen

private const val NAVIGATION_TRANSITION_DURATION_MILLIS = 300

@Suppress("LongMethod")
@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val navigationState =
        rememberNavigationState(
            startRoute = Route.Home,
            topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys,
        )
    val navigator = remember { Navigator(navigationState) }

    val onRestaurantAddedRef = remember { mutableStateOf<((Restaurant) -> Unit)?>(null) }

    val activeBackStack = navigationState.backStacks[navigationState.topLevelRoute]
    val showBottomBar = (activeBackStack?.size ?: 0) <= 1

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    selectedKey = navigationState.topLevelRoute,
                    onSelectKey = { navigator.navigate(it) },
                )
            }
        },
    ) { innerPadding ->
        NavDisplay(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            onBack = navigator::goBack,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
                ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth / 4 },
                        animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
                    )
            },
            popTransitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 4 },
                    animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
                ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
                    )
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 4 },
                    animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
                ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(NAVIGATION_TRANSITION_DURATION_MILLIS),
                    )
            },
            entries =
                navigationState.toEntries(
                    entryProvider {
                        entry<Route.Home> {
                            HomeEntry(navigator)
                        }
                        entry<Route.Guides> {
                            GuidesEntry(navigator)
                        }
                        entry<Route.Authors> {
                            AuthorsEntry(navigator)
                        }
                        entry<Route.Profile> {
                            ProfileEntry(navigator)
                        }
                        entry<Route.EditProfile> {
                            EditProfileEntry(navigator)
                        }
                        entry<Route.FavouriteGuides> {
                            FavouriteGuidesEntry(navigator)
                        }
                        entry<Route.FavouriteRestaurants> {
                            FavouriteRestaurantsEntry(navigator)
                        }
                        entry<Route.GuideDetail> { route ->
                            GuideDetailEntry(route, navigator)
                        }
                        entry<Route.EditGuide> { route ->
                            EditGuideEntry(route, navigator, onRestaurantAddedRef)
                        }
                        entry<Route.AuthorDetail> { route ->
                            AuthorDetailEntry(route, navigator)
                        }
                        entry<Route.CreateGuide> {
                            CreateGuideEntry(navigator)
                        }
                        entry<Route.RestaurantDetail> { route ->
                            RestaurantDetailEntry(route, navigator)
                        }
                        entry<Route.AddRestaurants> { route ->
                            SearchRestaurantsEntry(route, navigator, onRestaurantAddedRef)
                        }
                    },
                ),
        )
    }
}

@Composable
private fun HomeEntry(navigator: Navigator) {
    HomeScreen(
        onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
        onRestaurantClick = { restaurantId ->
            navigator.navigate(Route.RestaurantDetail(restaurantId))
        },
    )
}

@Composable
private fun GuidesEntry(navigator: Navigator) {
    GuidesScreen(
        onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
        onAddClick = { navigator.navigate(Route.CreateGuide) },
    )
}

@Composable
private fun AuthorsEntry(navigator: Navigator) {
    AuthorsScreen(
        onAuthorClick = { authorId ->
            navigator.navigate(
                Route.AuthorDetail(
                    authorId,
                ),
            )
        },
    )
}

@Composable
private fun ProfileEntry(navigator: Navigator) {
    ProfileScreen(
        onEditProfileClick = { navigator.navigate(Route.EditProfile) },
        onFavouriteGuidesClick = { navigator.navigate(Route.FavouriteGuides) },
        onFavouriteRestaurantsClick = { navigator.navigate(Route.FavouriteRestaurants) },
    )
}

@Composable
private fun EditProfileEntry(navigator: Navigator) {
    EditProfileScreen(onBackClick = navigator::goBack)
}

@Composable
private fun FavouriteGuidesEntry(navigator: Navigator) {
    FavouriteGuidesScreen(
        onBackClick = navigator::goBack,
        onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
    )
}

@Composable
private fun FavouriteRestaurantsEntry(navigator: Navigator) {
    FavouriteRestaurantsScreen(
        onBackClick = navigator::goBack,
        onRestaurantClick = { restaurantId ->
            navigator.navigate(Route.RestaurantDetail(restaurantId))
        },
    )
}

@Composable
private fun GuideDetailEntry(
    route: Route.GuideDetail,
    navigator: Navigator,
) {
    GuideDetailScreen(
        guideId = route.guideId,
        onBackClick = navigator::goBack,
        onEditClick = { guideId -> navigator.navigate(Route.EditGuide(guideId)) },
        onRestaurantClick = { restaurantId ->
            navigator.navigate(Route.RestaurantDetail(restaurantId))
        },
        onAuthorClick = { authorId -> navigator.navigate(Route.AuthorDetail(authorId)) },
    )
}

@Composable
private fun EditGuideEntry(
    route: Route.EditGuide,
    navigator: Navigator,
    onRestaurantAddedRef: MutableState<((Restaurant) -> Unit)?>,
) {
    EditGuideScreen(
        guideId = route.guideId,
        onBackClick = navigator::goBack,
        onGuideDeleted = navigator::popToRoot,
        initialTab = route.initialTab,
        onAddRestaurantsClick = { onRestaurantAdded ->
            onRestaurantAddedRef.value = onRestaurantAdded
            navigator.navigate(Route.AddRestaurants(route.guideId))
        },
    )
}

@Composable
private fun AuthorDetailEntry(
    route: Route.AuthorDetail,
    navigator: Navigator,
) {
    AuthorDetailScreen(
        authorId = route.authorId,
        onBackClick = navigator::goBack,
        onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
    )
}

@Composable
private fun CreateGuideEntry(navigator: Navigator) {
    CreateGuideScreen(
        onBackClick = navigator::goBack,
        onGuideCreated = { guideId ->
            navigator.goBack()
            navigator.navigate(Route.EditGuide(guideId, initialTab = 1))
        },
    )
}

@Composable
private fun RestaurantDetailEntry(
    route: Route.RestaurantDetail,
    navigator: Navigator,
) {
    RestaurantDetailScreen(
        restaurantId = route.restaurantId,
        onBackClick = navigator::goBack,
    )
}

@Composable
fun SearchRestaurantsEntry(
    route: Route.AddRestaurants,
    navigator: Navigator,
    onRestaurantAddedRef: MutableState<((Restaurant) -> Unit)?>,
) {
    SearchRestaurantsScreen(
        guideId = route.guideId,
        onBackClick = navigator::goBack,
        onRestaurantAdded = { restaurant ->
            onRestaurantAddedRef.value?.invoke(restaurant)
            navigator.goBack()
        },
    )
}
