package pt.socialfood.presentation.restaurant.wishlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.presentation.components.PullToRefreshContent
import pt.socialfood.presentation.restaurant.RestaurantSmallCard
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.back_button_description
import socialfood.composeapp.generated.resources.wish_add_button_description
import socialfood.composeapp.generated.resources.wish_card_remove_button_description
import socialfood.composeapp.generated.resources.wish_restaurants_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantWishlistScreen(
    onBackClick: () -> Unit,
    onRestaurantClick: (restaurantId: String) -> Unit = {},
    onAddClick: (onRestaurantAdded: (Restaurant) -> Unit) -> Unit = {},
    viewModel: RestaurantWishlistViewModel = koinViewModel(),
) {
    val restaurants = viewModel.restaurants.collectAsLazyPagingItems()

    RestaurantWishlistContent(
        restaurants = restaurants,
        onBackClick = onBackClick,
        onRestaurantClick = onRestaurantClick,
        onAddClick = { onAddClick(viewModel::addToWishlist) },
        onRemoveClick = viewModel::removeFromWishlist,
    )
}

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RestaurantWishlistContent(
    restaurants: LazyPagingItems<Restaurant>,
    onBackClick: () -> Unit,
    onRestaurantClick: (restaurantId: String) -> Unit = {},
    onAddClick: () -> Unit = {},
    onRemoveClick: (restaurantId: String) -> Unit = {},
) {
    val listState = rememberLazyListState()
    val isRefreshing = restaurants.loadState.refresh is LoadState.Loading && restaurants.itemCount > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        TopBar(onBackClick = onBackClick, onAddClick = onAddClick)

        when {
            restaurants.loadState.refresh is LoadState.Loading && restaurants.itemCount == 0 ->
                RestaurantWishlistSkeleton(modifier = Modifier.fillMaxSize())

            restaurants.loadState.refresh is LoadState.Error && restaurants.itemCount == 0 -> ErrorContent(
                modifier = Modifier.fillMaxSize(),
                onRetryClick = { restaurants.retry() },
            )

            restaurants.loadState.refresh is LoadState.NotLoading &&
                restaurants.loadState.append.endOfPaginationReached &&
                restaurants.itemCount == 0 -> NoResultsContent(modifier = Modifier.fillMaxSize())

            else -> PullToRefreshContent(
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
                    items(count = restaurants.itemCount, key = restaurants.itemKey { it.id }) { index ->
                        restaurants[index]?.let { restaurant ->
                            RestaurantSmallCard(
                                restaurant = restaurant,
                                removeButtonContentDescription = stringResource(
                                    Res.string.wish_card_remove_button_description,
                                ),
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
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(SpaceSize.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.back_button_description),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Text(
            text = stringResource(Res.string.wish_restaurants_title),
            style = AppTypography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onAddClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.wish_add_button_description),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview
@Composable
private fun RestaurantWishlistScreenLoadedPreview() {
    val restaurants = listOf(
        Restaurant(
            id = "r1",
            name = "Le Jardin",
            description = "A charming garden restaurant with French-inspired cuisine",
            city = "Lisbon",
            country = "Portugal",
            countryCode = "PT",
            postalCode = "1000-000",
            photoNames = emptyList(),
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
            photoNames = emptyList(),
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
        RestaurantWishlistContent(
            restaurants = items,
            onBackClick = {},
        )
    }
}

@Preview
@Composable
private fun RestaurantWishlistScreenEmptyPreview() {
    val emptyLoadState = LoadState.NotLoading(endOfPaginationReached = true)
    val items = flowOf(
        PagingData.empty<Restaurant>(
            sourceLoadStates = LoadStates(refresh = emptyLoadState, prepend = emptyLoadState, append = emptyLoadState),
        ),
    ).collectAsLazyPagingItems()

    AppTheme {
        RestaurantWishlistContent(
            restaurants = items,
            onBackClick = {},
        )
    }
}
