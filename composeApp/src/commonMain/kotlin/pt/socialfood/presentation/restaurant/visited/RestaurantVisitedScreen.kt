package pt.socialfood.presentation.restaurant.visited

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.presentation.restaurant.RestaurantSmallCard
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.back_button_description
import socialfood.composeapp.generated.resources.visited_card_remove_button_description
import socialfood.composeapp.generated.resources.visited_restaurants_title

private const val LOAD_MORE_THRESHOLD = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantVisitedScreen(
    onBackClick: () -> Unit,
    onRestaurantClick: (restaurantId: String) -> Unit = {},
    viewModel: RestaurantVisitedViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    VisitedRestaurantsContent(
        state = state,
        isRefreshing = isRefreshing,
        onBackClick = onBackClick,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
        onRetry = viewModel::loadFirstPage,
        onRestaurantClick = onRestaurantClick,
        onRemoveClick = viewModel::removeFromVisited,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisitedRestaurantsContent(
    state: RestaurantVisitedUiState,
    isRefreshing: Boolean,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onRestaurantClick: (restaurantId: String) -> Unit = {},
    onRemoveClick: (restaurantId: String) -> Unit = {},
) {
    val listState = rememberLazyListState()

    val reachedBottom by remember(listState) {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible != null && totalItems > 0 && lastVisible.index >= totalItems - 1 - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(reachedBottom, state) {
        if (reachedBottom && state is RestaurantVisitedUiState.Loaded && state.hasMore && !state.isLoadingMore) {
            onLoadMore()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreyBackground),
    ) {
        TopBar(onBackClick = onBackClick)

        when (state) {
            RestaurantVisitedUiState.Loading -> RestaurantVisitedSkeleton(modifier = Modifier.fillMaxSize())

            is RestaurantVisitedUiState.Error -> ErrorContent(
                modifier = Modifier.fillMaxSize(),
                onRetryClick = onRetry,
            )

            is RestaurantVisitedUiState.Loaded -> if (state.restaurants.isEmpty()) {
                NoResultsContent(modifier = Modifier.fillMaxSize())
            } else {
                VisitedRestaurantsList(
                    restaurants = state.restaurants,
                    listState = listState,
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    onRestaurantClick = onRestaurantClick,
                    onRemoveClick = onRemoveClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisitedRestaurantsList(
    restaurants: List<Restaurant>,
    listState: LazyListState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onRestaurantClick: (restaurantId: String) -> Unit,
    onRemoveClick: (restaurantId: String) -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
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
            items(restaurants, key = { it.id }) { restaurant ->
                RestaurantSmallCard(
                    restaurant = restaurant,
                    removeButtonContentDescription = stringResource(
                        Res.string.visited_card_remove_button_description,
                    ),
                    onClick = { onRestaurantClick(restaurant.id) },
                    onRemoveClick = { onRemoveClick(restaurant.id) },
                )
            }
        }
    }
}

@Composable
private fun TopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
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
            text = stringResource(Res.string.visited_restaurants_title),
            style = AppTypography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
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
            photoNames = emptyList(),
            address = "Rua Augusta 123, Lisbon",
            rating = 4.8,
            userRatingCount = 320,
            websiteUrl = null,
            phoneNumber = "+351 910 000 000",
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
        ),
    )
    AppTheme {
        VisitedRestaurantsContent(
            state = RestaurantVisitedUiState.Loaded(restaurants = restaurants, hasMore = false),
            isRefreshing = false,
            onBackClick = {},
            onRefresh = {},
            onLoadMore = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun RestaurantVisitedScreenEmptyPreview() {
    AppTheme {
        VisitedRestaurantsContent(
            state = RestaurantVisitedUiState.Loaded(restaurants = emptyList(), hasMore = false),
            isRefreshing = false,
            onBackClick = {},
            onRefresh = {},
            onLoadMore = {},
            onRetry = {},
        )
    }
}
