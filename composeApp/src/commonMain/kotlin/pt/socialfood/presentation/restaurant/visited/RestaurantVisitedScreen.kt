package pt.socialfood.presentation.restaurant.visited

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.ActionButton
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.presentation.components.PullToRefreshContent
import pt.socialfood.presentation.components.TopActionBar
import pt.socialfood.presentation.components.buttons.OutlinedButton
import pt.socialfood.presentation.restaurant.RestaurantVisitStatusCard
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.guide_detail_map_button_description
import socialfood.composeapp.generated.resources.visited_no_results_subtitle
import socialfood.composeapp.generated.resources.visited_no_results_title
import socialfood.composeapp.generated.resources.visited_restaurants_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantVisitedScreen(
    onBackClick: () -> Unit,
    onRestaurantClick: (restaurantId: String) -> Unit = {},
    onAddClick: (onRestaurantAdded: (Restaurant) -> Unit) -> Unit = {},
    onMapClick: () -> Unit = {},
    viewModel: RestaurantVisitedViewModel = koinViewModel(),
) {
    val restaurants = viewModel.restaurants.collectAsLazyPagingItems()

    VisitedRestaurantsContent(
        restaurants = restaurants,
        onBackClick = onBackClick,
        onRestaurantClick = onRestaurantClick,
        onAddClick = { onAddClick(viewModel::addToVisited) },
        onRemoveClick = viewModel::removeFromVisited,
        onMapClick = onMapClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisitedRestaurantsContent(
    restaurants: LazyPagingItems<Restaurant>,
    onBackClick: () -> Unit,
    onRestaurantClick: (restaurantId: String) -> Unit = {},
    onAddClick: () -> Unit = {},
    onRemoveClick: (restaurantId: String) -> Unit = {},
    onMapClick: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    val isRefreshing = restaurants.loadState.refresh is LoadState.Loading && restaurants.itemCount > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        TopActionBar(
            title = stringResource(Res.string.visited_restaurants_title),
            onBackClick = onBackClick,
            actionButton = ActionButton.Add,
            onActionClick = onAddClick,
        )

        when (restaurants.loadState.refresh) {
            is LoadState.Loading if restaurants.itemCount == 0 ->
                RestaurantVisitedSkeleton(modifier = Modifier.fillMaxSize())

            is LoadState.Error if restaurants.itemCount == 0 -> ErrorContent(
                modifier = Modifier.fillMaxSize(),
                onRetryClick = { restaurants.retry() },
            )

            is LoadState.NotLoading if restaurants.loadState.append.endOfPaginationReached &&
                restaurants.itemCount == 0 -> NoResultsContent(
                title = stringResource(Res.string.visited_no_results_title),
                subtitle = stringResource(Res.string.visited_no_results_subtitle),
                modifier = Modifier.fillMaxSize(),
            )

            else -> VisitedRestaurantsList(
                restaurants = restaurants,
                listState = listState,
                isRefreshing = isRefreshing,
                onRestaurantClick = onRestaurantClick,
                onRemoveClick = onRemoveClick,
                onMapClick = onMapClick,
            )
        }
    }
}

@Composable
private fun VisitedRestaurantsList(
    restaurants: LazyPagingItems<Restaurant>,
    listState: LazyListState,
    isRefreshing: Boolean,
    onRestaurantClick: (restaurantId: String) -> Unit,
    onRemoveClick: (restaurantId: String) -> Unit,
    onMapClick: () -> Unit,
) {
    PullToRefreshContent(
        isRefreshing = isRefreshing,
        onRefresh = { restaurants.refresh() },
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = SpaceSize.large,
                vertical = SpaceSize.large,
            ),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            item { MapButtonItem(onClick = onMapClick) }

            items(count = restaurants.itemCount, key = restaurants.itemKey { it.id }) { index ->
                restaurants[index]?.let { restaurant ->
                    RestaurantVisitStatusCard(
                        restaurant = restaurant,
                        onClick = { onRestaurantClick(restaurant.id) },
                        onRemoveClick = { onRemoveClick(restaurant.id) },
                    )
                }
            }

            if (restaurants.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(SpaceSize.large),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun MapButtonItem(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = SpaceSize.small),
        contentAlignment = Alignment.CenterEnd,
    ) {
        OutlinedButton(
            icon = Icons.Outlined.Map,
            text = stringResource(Res.string.guide_detail_map_button_description),
            onClick = onClick,
        )
    }
}

@Preview
@Composable
private fun RestaurantVisitedScreenLoadedPreview() {
    val restaurants = listOf(
        Restaurant(
            id = "r1",
            name = "Le Jardin",
            description = "A charming garden restaurant with French-inspired cuisine",
            city = "Lisbon",
            country = "Portugal",
            countryCode = "PT",
            postalCode = "1000-000",
            imagesUrl = emptyList(),
            address = "Rua Augusta 123, Lisbon",
            rating = 4.8,
            userRatingCount = 320,
            websiteUrl = null,
            phoneNumber = "+351 910 000 000",
            location = Location(latitude = 38.7223, longitude = -9.1393),
        ),
        Restaurant(
            id = "r2",
            name = "Taberna do Mar",
            description = "Fresh seafood by the docks",
            city = "Porto",
            country = "Portugal",
            countryCode = "PT",
            postalCode = "4000-000",
            imagesUrl = emptyList(),
            address = "Rua Nova 45, Porto",
            rating = 4.5,
            userRatingCount = 210,
            websiteUrl = null,
            phoneNumber = "+351 920 000 000",
            location = Location(latitude = 41.1579, longitude = -8.6291),
        ),
    )
    val items = flowOf(PagingData.from(restaurants)).collectAsLazyPagingItems()

    AppTheme {
        VisitedRestaurantsContent(
            restaurants = items,
            onBackClick = {},
        )
    }
}

@Preview
@Composable
private fun RestaurantVisitedScreenEmptyPreview() {
    val emptyLoadState = LoadState.NotLoading(endOfPaginationReached = true)
    val items = flowOf(
        PagingData.empty<Restaurant>(
            sourceLoadStates = LoadStates(refresh = emptyLoadState, prepend = emptyLoadState, append = emptyLoadState),
        ),
    ).collectAsLazyPagingItems()

    AppTheme {
        VisitedRestaurantsContent(
            restaurants = items,
            onBackClick = {},
        )
    }
}
