package pt.socialfood.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.presentation.author.detail.AuthorDetailScreen
import pt.socialfood.presentation.author.list.AuthorsScreen
import pt.socialfood.presentation.drawer.DrawerContent
import pt.socialfood.presentation.favourite.guide.FavouriteGuidesScreen
import pt.socialfood.presentation.favourite.restaurant.FavouriteRestaurantsScreen
import pt.socialfood.presentation.guide.GuidesScreen
import pt.socialfood.presentation.guide.create.CreateGuideScreen
import pt.socialfood.presentation.guide.detail.GuideDetailScreen
import pt.socialfood.presentation.guide.edit.EditGuideScreen
import pt.socialfood.presentation.guide.map.GuideMapScreen
import pt.socialfood.presentation.home.HomeScreen
import pt.socialfood.presentation.map.restaurant.MapVisitRestaurantScreen
import pt.socialfood.presentation.profile.edit.EditProfileScreen
import pt.socialfood.presentation.restaurant.detail.RestaurantDetailScreen
import pt.socialfood.presentation.restaurant.search.SearchRestaurantsScreen
import pt.socialfood.presentation.restaurant.visited.RestaurantVisitedScreen
import pt.socialfood.presentation.restaurant.wishlist.RestaurantWishlistScreen
import pt.socialfood.presentation.search.SearchScreen
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.visited_restaurants_title
import socialfood.composeapp.generated.resources.wish_restaurants_title

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

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DrawerContent(
                onProfileClick = { authorId ->
                    scope.launch { drawerState.close() }
                    navigator.navigate(Route.AuthorDetail(authorId))
                },
                onEditProfileClick = {
                    scope.launch { drawerState.close() }
                    navigator.navigate(Route.EditProfile)
                },
                onFavouriteGuidesClick = {
                    scope.launch { drawerState.close() }
                    navigator.navigate(Route.FavouriteGuides)
                },
                onFavouriteRestaurantsClick = {
                    scope.launch { drawerState.close() }
                    navigator.navigate(Route.FavouriteRestaurants)
                },
                onWishRestaurantsClick = {
                    scope.launch { drawerState.close() }
                    navigator.navigate(Route.WishRestaurants)
                },
                onVisitedRestaurantsClick = {
                    scope.launch { drawerState.close() }
                    navigator.navigate(Route.VisitedRestaurants)
                },
            )
        },
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onBack = navigator::goBack,
                entries = navigationState.toEntries(
                    entryProvider {
                        entry<Route.AuthorDetail>(metadata = defaultAnimationMetadata) { route ->
                            AuthorDetailScreen(
                                authorId = route.authorId,
                                onBackClick = navigator::goBack,
                                onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
                            )
                        }
                        entry<Route.Authors>(metadata = defaultAnimationMetadata) {
                            AuthorsScreen(
                                onAuthorClick = { authorId ->
                                    navigator.navigate(
                                        Route.AuthorDetail(
                                            authorId,
                                        ),
                                    )
                                },
                                onProfileClick = { scope.launch { drawerState.open() } },
                            )
                        }
                        entry<Route.CreateGuide> {
                            CreateGuideScreen(
                                onBackClick = navigator::goBack,
                                onGuideCreated = { guideId ->
                                    navigator.goBack()
                                    navigator.navigate(Route.EditGuide(guideId, initialTab = 1))
                                },
                            )
                        }
                        entry<Route.EditGuide> { route ->
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
                        entry<Route.GuideDetail>(metadata = defaultAnimationMetadata) { route ->
                            GuideDetailScreen(
                                guideId = route.guideId,
                                onBackClick = navigator::goBack,
                                onEditClick = { guideId -> navigator.navigate(Route.EditGuide(guideId)) },
                                onRestaurantClick = { restaurantId ->
                                    navigator.navigate(Route.RestaurantDetail(restaurantId))
                                },
                                onAuthorClick = { authorId -> navigator.navigate(Route.AuthorDetail(authorId)) },
                                onViewMapClick = { guideId, guideName, restaurantsCount ->
                                    navigator.navigate(Route.GuideMap(guideId, guideName, restaurantsCount))
                                },
                            )
                        }
                        entry<Route.GuideMap>(metadata = slideUpAnimationMetadata) { route ->
                            GuideMapScreen(
                                guideId = route.guideId,
                                guideName = route.guideName,
                                restaurantsCount = route.restaurantsCount,
                                onBackClick = navigator::goBack,
                                onRestaurantClick = { restaurantId ->
                                    navigator.navigate(Route.RestaurantDetail(restaurantId))
                                },
                            )
                        }
                        entry<Route.Guides>(metadata = defaultAnimationMetadata) {
                            GuidesScreen(
                                onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
                                onAddClick = { navigator.navigate(Route.CreateGuide) },
                                onProfileClick = { scope.launch { drawerState.open() } },
                                onGuideJoined = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
                            )
                        }
                        entry<Route.FavouriteGuides>(metadata = slideHorizontalAnimationMetadata) {
                            FavouriteGuidesScreen(
                                onBackClick = navigator::goBack,
                                onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
                            )
                        }
                        entry<Route.FavouriteRestaurants>(metadata = slideHorizontalAnimationMetadata) {
                            FavouriteRestaurantsScreen(
                                onBackClick = navigator::goBack,
                                onRestaurantClick = { restaurantId ->
                                    navigator.navigate(Route.RestaurantDetail(restaurantId))
                                },
                            )
                        }
                        entry<Route.WishRestaurants>(metadata = slideHorizontalAnimationMetadata) {
                            RestaurantWishlistScreen(
                                onBackClick = navigator::goBack,
                                onRestaurantClick = { restaurantId ->
                                    navigator.navigate(Route.RestaurantDetail(restaurantId))
                                },
                                onAddClick = { onRestaurantAdded ->
                                    onRestaurantAddedRef.value = onRestaurantAdded
                                    navigator.navigate(Route.AddWishRestaurant)
                                },
                                onMapClick = { navigator.navigate(Route.RestaurantsMap(VisitStatus.WISHLIST)) },
                            )
                        }
                        entry<Route.VisitedRestaurants>(metadata = slideHorizontalAnimationMetadata) {
                            RestaurantVisitedScreen(
                                onBackClick = navigator::goBack,
                                onRestaurantClick = { restaurantId ->
                                    navigator.navigate(Route.RestaurantDetail(restaurantId))
                                },
                                onAddClick = { onRestaurantAdded ->
                                    onRestaurantAddedRef.value = onRestaurantAdded
                                    navigator.navigate(Route.AddVisitedRestaurant)
                                },
                                onMapClick = { navigator.navigate(Route.RestaurantsMap(VisitStatus.VISITED)) },
                            )
                        }
                        entry<Route.RestaurantsMap>(metadata = slideUpAnimationMetadata) { route ->
                            MapVisitRestaurantScreen(
                                status = route.status,
                                title = when (route.status) {
                                    VisitStatus.WISHLIST -> stringResource(Res.string.wish_restaurants_title)
                                    VisitStatus.VISITED -> stringResource(Res.string.visited_restaurants_title)
                                },
                                onBackClick = navigator::goBack,
                                onRestaurantClick = { restaurantId ->
                                    navigator.navigate(Route.RestaurantDetail(restaurantId))
                                },
                            )
                        }
                        entry<Route.Home>(metadata = defaultAnimationMetadata) {
                            HomeScreen(
                                onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
                                onRestaurantClick = { restaurantId ->
                                    navigator.navigate(Route.RestaurantDetail(restaurantId))
                                },
                                onProfileClick = { scope.launch { drawerState.open() } },
                                onSearchClick = { navigator.navigate(Route.Search) },
                            )
                        }
                        entry<Route.EditProfile>(metadata = slideHorizontalAnimationMetadata) {
                            EditProfileScreen(onBackClick = navigator::goBack)
                        }
                        entry<Route.AddRestaurants> { route ->
                            SearchRestaurantsScreen(
                                guideId = route.guideId,
                                onBackClick = navigator::goBack,
                                onRestaurantAdded = { restaurant ->
                                    onRestaurantAddedRef.value?.invoke(restaurant)
                                    navigator.goBack()
                                },
                            )
                        }
                        entry<Route.AddWishRestaurant> {
                            SearchRestaurantsScreen(
                                guideId = "",
                                onBackClick = navigator::goBack,
                                onRestaurantAdded = { restaurant ->
                                    onRestaurantAddedRef.value?.invoke(restaurant)
                                    navigator.goBack()
                                },
                            )
                        }
                        entry<Route.AddVisitedRestaurant> {
                            SearchRestaurantsScreen(
                                guideId = "",
                                onBackClick = navigator::goBack,
                                onRestaurantAdded = { restaurant ->
                                    onRestaurantAddedRef.value?.invoke(restaurant)
                                    navigator.goBack()
                                },
                            )
                        }
                        entry<Route.RestaurantDetail>(metadata = defaultAnimationMetadata) { route ->
                            RestaurantDetailScreen(
                                restaurantId = route.restaurantId,
                                onBackClick = navigator::goBack,
                            )
                        }
                        entry<Route.Search>(metadata = defaultAnimationMetadata) {
                            SearchScreen(
                                onAuthorClick = { authorId -> navigator.navigate(Route.AuthorDetail(authorId)) },
                                onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
                                onRestaurantClick = { restaurantId ->
                                    navigator.navigate(Route.RestaurantDetail(restaurantId))
                                },
                            )
                        }
                    },
                ),
            )
        }
    }
}
