package pt.socialfood.presentation.favourite.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.back_button_description
import socialfood.composeapp.generated.resources.favourites_guides_title

private const val LOAD_MORE_THRESHOLD = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteGuidesScreen(
    onBackClick: () -> Unit,
    onGuideClick: (guideId: String) -> Unit = {},
    viewModel: FavouriteGuidesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    FavouriteGuidesContent(
        state = state,
        isRefreshing = isRefreshing,
        onBackClick = onBackClick,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
        onRetry = viewModel::loadFirstPage,
        onGuideClick = onGuideClick,
        onRemoveClick = viewModel::removeFavourite,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavouriteGuidesContent(
    state: FavouriteGuidesUiState,
    isRefreshing: Boolean,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onGuideClick: (guideId: String) -> Unit = {},
    onRemoveClick: (guideId: String) -> Unit = {},
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
        if (reachedBottom && state is FavouriteGuidesUiState.Loaded && state.hasMore && !state.isLoadingMore) {
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
            FavouriteGuidesUiState.Loading -> FavouriteGuidesPlaceholder(modifier = Modifier.fillMaxSize())

            is FavouriteGuidesUiState.Error -> ErrorContent(
                modifier = Modifier.fillMaxSize(),
                onRetryClick = onRetry,
            )

            is FavouriteGuidesUiState.Loaded -> if (state.guides.isEmpty()) {
                NoResultsContent(modifier = Modifier.fillMaxSize())
            } else {
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
                        items(state.guides, key = { it.id }) { guide ->
                            FavoriteGuideCard(
                                guide = guide,
                                onClick = { onGuideClick(guide.id) },
                                onRemoveClick = { onRemoveClick(guide.id) },
                            )
                        }
                    }
                }
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
            text = stringResource(Res.string.favourites_guides_title),
            style = AppTypography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview
@Composable
private fun FavouriteGuidesScreenLoadedPreview() {
    val guides = listOf(
        Guide(
            id = "g1",
            name = "Michelin Star Favorites",
            description = "A curated collection of the finest dining experiences",
            visibility = GuideVisibility.PUBLIC,
            author = Author(id = "a1", name = "Sarah Mitchell", username = "sarahmitchell"),
            numberOfRestaurant = 8,
        ),
        Guide(
            id = "g2",
            name = "Hidden Gems Lisbon",
            description = "Off the beaten path restaurants in Lisbon",
            visibility = GuideVisibility.PUBLIC,
            author = Author(id = "a2", name = "Michael Rodriguez", username = "michaelrodriguez"),
            numberOfRestaurant = 5,
        ),
    )
    AppTheme {
        FavouriteGuidesContent(
            state = FavouriteGuidesUiState.Loaded(guides = guides, hasMore = false),
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
private fun FavouriteGuidesScreenEmptyPreview() {
    AppTheme {
        FavouriteGuidesContent(
            state = FavouriteGuidesUiState.Loaded(guides = emptyList(), hasMore = false),
            isRefreshing = false,
            onBackClick = {},
            onRefresh = {},
            onLoadMore = {},
            onRetry = {},
        )
    }
}
