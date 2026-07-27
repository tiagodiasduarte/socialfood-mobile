package pt.socialfood.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.NonCancellable.key
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.authors.detail.AuthorDetailScreen
import pt.socialfood.presentation.authors.detail.AuthorDetailViewModel
import pt.socialfood.presentation.authors.AuthorsScreen
import pt.socialfood.presentation.authors.AuthorsViewModel
import pt.socialfood.presentation.favourite.guide.FavouriteGuidesScreen
import pt.socialfood.presentation.favourite.restaurant.FavouriteRestaurantsScreen
import pt.socialfood.presentation.guide.detail.GuideDetailScreen
import pt.socialfood.presentation.guide.detail.GuideDetailViewModel
import pt.socialfood.presentation.guide.list.GuidesScreen
import pt.socialfood.presentation.guide.list.GuidesViewModel
import pt.socialfood.presentation.guide.create.CreateGuideScreen
import pt.socialfood.presentation.guide.create.CreateGuideViewModel
import pt.socialfood.presentation.restaurant.search.SearchRestaurantsScreen
import pt.socialfood.presentation.restaurant.search.SearchRestaurantsViewModel
import pt.socialfood.presentation.guide.edit.EditGuideScreen
import pt.socialfood.presentation.guide.edit.EditGuideViewModel
import pt.socialfood.presentation.home.HomeScreen
import pt.socialfood.presentation.profile.ProfileScreen
import pt.socialfood.presentation.profile.edit.EditProfileScreen
import pt.socialfood.presentation.restaurant.detail.RestaurantDetailScreen
import pt.socialfood.presentation.restaurant.detail.RestaurantDetailViewModel

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
) {
    val navigationState = rememberNavigationState(
        startRoute = Route.Home,
        topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys
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
                    onSelectKey = { navigator.navigate(it) }
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onBack = navigator::goBack,
            entries = navigationState.toEntries(
                entryProvider {
                    entry<Route.Home> {
                        HomeScreen(
                            onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
                            onRestaurantClick = { restaurantId -> navigator.navigate(Route.RestaurantDetail(restaurantId)) },
                        )
                    }
                    entry<Route.Guides> {
                        val vm = remember(key) { getKoin().get<GuidesViewModel>() }
                        GuidesScreen(
                            viewModel = vm,
                            onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
                            onAddClick = { navigator.navigate(Route.CreateGuide) },
                        )
                    }
                    entry<Route.Authors> {
                        val vm = remember(key) { getKoin().get<AuthorsViewModel>() }
                        AuthorsScreen(
                            viewModel = vm,
                            onAuthorClick = { authorId ->
                                navigator.navigate(
                                    Route.AuthorDetail(
                                        authorId
                                    )
                                )
                            },
                        )
                    }
                    entry<Route.Profile> {
                        ProfileScreen(
                            onEditProfileClick = { navigator.navigate(Route.EditProfile) },
                            onFavouriteGuidesClick = { navigator.navigate(Route.FavouriteGuides) },
                            onFavouriteRestaurantsClick = { navigator.navigate(Route.FavouriteRestaurants) },
                        )
                    }
                    entry<Route.EditProfile> {
                        EditProfileScreen(onBackClick = navigator::goBack)
                    }
                    entry<Route.FavouriteGuides> {
                        FavouriteGuidesScreen(
                            onBackClick = navigator::goBack,
                            onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
                        )
                    }
                    entry<Route.FavouriteRestaurants> {
                        FavouriteRestaurantsScreen(
                            onBackClick = navigator::goBack,
                            onRestaurantClick = { restaurantId -> navigator.navigate(Route.RestaurantDetail(restaurantId)) },
                        )
                    }
                    entry<Route.GuideDetail> { route ->
                        val vm = remember(route.guideId) {
                            getKoin().get<GuideDetailViewModel> {
                                parametersOf(route.guideId)
                            }
                        }
                        GuideDetailScreen(
                            guideId = route.guideId,
                            onBackClick = navigator::goBack,
                            onEditClick = { guideId -> navigator.navigate(Route.EditGuide(guideId)) },
                            onRestaurantClick = { restaurantId -> navigator.navigate(Route.RestaurantDetail(restaurantId)) },
                            onAuthorClick = { authorId -> navigator.navigate(Route.AuthorDetail(authorId)) },
                            viewModel = vm,
                        )
                    }
                    entry<Route.EditGuide> { route ->
                        val vm = koinViewModel<EditGuideViewModel>(
                            parameters = { parametersOf(route.guideId) }
                        )
                        EditGuideScreen(
                            onBackClick = navigator::goBack,
                            onGuideDeleted = navigator::popToRoot,
                            initialTab = route.initialTab,
                            onAddRestaurantsClick = { onRestaurantAdded ->
                                onRestaurantAddedRef.value = onRestaurantAdded
                                navigator.navigate(Route.AddRestaurants(route.guideId))
                            },
                            viewModel = vm,
                        )
                    }
                    entry<Route.AuthorDetail> { route ->
                        val vm = remember(route.authorId) {
                            getKoin().get<AuthorDetailViewModel> {
                                parametersOf(route.authorId)
                            }
                        }
                        AuthorDetailScreen(
                            authorId = route.authorId,
                            onBackClick = navigator::goBack,
                            onGuideClick = { guideId -> navigator.navigate(Route.GuideDetail(guideId)) },
                            viewModel = vm,
                        )
                    }
                    entry<Route.CreateGuide> {
                        val vm = koinViewModel<CreateGuideViewModel>()
                        CreateGuideScreen(
                            viewModel = vm,
                            onBackClick = navigator::goBack,
                            onGuideCreated = { guideId ->
                                navigator.goBack()
                                navigator.navigate(Route.EditGuide(guideId, initialTab = 1))
                            },
                        )
                    }
                    entry<Route.RestaurantDetail> { route ->
                        val vm = remember(route.restaurantId) {
                            getKoin().get<RestaurantDetailViewModel> {
                                parametersOf(route.restaurantId)
                            }
                        }
                        RestaurantDetailScreen(
                            restaurantId = route.restaurantId,
                            onBackClick = navigator::goBack,
                            viewModel = vm,
                        )
                    }
                    entry<Route.AddRestaurants> { route ->
                        val vm = koinViewModel<SearchRestaurantsViewModel>(
                            parameters = { parametersOf(route.guideId) }
                        )
                        SearchRestaurantsScreen(
                            viewModel = vm,
                            onBackClick = navigator::goBack,
                            onRestaurantAdded = { restaurant ->
                                onRestaurantAddedRef.value?.invoke(restaurant)
                                navigator.goBack()
                            },
                        )
                    }
                }
            )
        )
    }
}
