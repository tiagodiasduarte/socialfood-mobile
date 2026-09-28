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
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.author.navigation.AuthorRoute
import pt.socialfood.presentation.author.navigation.authorEntries
import pt.socialfood.presentation.drawer.DrawerContent
import pt.socialfood.presentation.favourite.navigation.FavouriteRoute
import pt.socialfood.presentation.favourite.navigation.favouriteEntries
import pt.socialfood.presentation.guide.navigation.guideEntries
import pt.socialfood.presentation.home.navigation.HomeRoute
import pt.socialfood.presentation.home.navigation.homeEntries
import pt.socialfood.presentation.map.navigation.mapEntries
import pt.socialfood.presentation.profile.navigation.profileEntries
import pt.socialfood.presentation.restaurant.navigation.RestaurantRoute
import pt.socialfood.presentation.restaurant.navigation.restaurantEntries
import pt.socialfood.presentation.search.navigation.searchEntries

@Suppress("LongMethod")
@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val navigationState =
        rememberNavigationState(
            startRoute = HomeRoute.Home,
            topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys,
        )
    val navigator = remember { Navigator(navigationState) }

    val onRestaurantAddedRef = remember { mutableStateOf<((Restaurant) -> Unit)?>(null) }

    val activeBackStack = navigationState.backStacks[navigationState.topLevelRoute]
    val showBottomBar = (activeBackStack?.size ?: 0) <= 1

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val onOpenDrawer: () -> Unit = { scope.launch { drawerState.open() } }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DrawerContent(
                onProfileClick = { authorId ->
                    scope.launch { drawerState.close() }
                    navigator.navigate(AuthorRoute.Profile(authorId))
                },
                onFavouriteGuidesClick = {
                    scope.launch { drawerState.close() }
                    navigator.navigate(FavouriteRoute.FavouriteGuides)
                },
                onFavouriteRestaurantsClick = {
                    scope.launch { drawerState.close() }
                    navigator.navigate(FavouriteRoute.FavouriteRestaurants)
                },
                onWishRestaurantsClick = {
                    scope.launch { drawerState.close() }
                    navigator.navigate(RestaurantRoute.WishRestaurants)
                },
                onVisitedRestaurantsClick = {
                    scope.launch { drawerState.close() }
                    navigator.navigate(RestaurantRoute.VisitedRestaurants)
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
                        authorEntries(navigator, onOpenDrawer)
                        favouriteEntries(navigator)
                        guideEntries(navigator, onOpenDrawer, onRestaurantAddedRef)
                        homeEntries(navigator, onOpenDrawer)
                        mapEntries(navigator)
                        profileEntries(navigator)
                        restaurantEntries(navigator, onRestaurantAddedRef)
                        searchEntries(navigator)
                    },
                ),
            )
        }
    }
}
