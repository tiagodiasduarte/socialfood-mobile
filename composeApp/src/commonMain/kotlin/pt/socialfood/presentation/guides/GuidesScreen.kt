package pt.socialfood.presentation.guides

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

private const val LOAD_MORE_THRESHOLD = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidesScreen(
    viewModel: GuidesViewModel = koinViewModel(),
    onGuideClick: (guideId: String) -> Unit = {},
    onAddClick: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    GuidesScreenContent(
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        onLoadMore = { viewModel.loadMore() },
        onGuideClick = onGuideClick,
        onAddClick = onAddClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidesScreenContent(
    state: GuidesUiState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit = {},
    onGuideClick: (guideId: String) -> Unit = {},
    onAddClick: () -> Unit = {},
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val reachedBottom by remember(listState) {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible != null && totalItems > 0 && lastVisible.index >= totalItems - 1 - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(reachedBottom, state) {
        if (reachedBottom && state is GuidesUiState.Loaded && state.hasMore && !state.isLoadingMore) {
            onLoadMore()
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier
            .fillMaxSize()
            .background(GreyBackground),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = SpaceSize.xxlarge),
        ) {
            item {
                GuidesHeader(
                    selectedTab = selectedTab,
                    searchQuery = searchQuery,
                    onSelectedTab = { selectedTab = it },
                    onQueryChange = { searchQuery = it },
                    onAddClick = onAddClick,
                )
            }

            when (state) {
                GuidesUiState.Loading -> item {
                    GuidesPlaceholder()
                }

                is GuidesUiState.Loaded -> {
                    val baseList = if (selectedTab == 0) state.allGuides else state.myGuides
                    val guides = if (searchQuery.isBlank()) baseList
                    else baseList.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                                it.description.contains(searchQuery, ignoreCase = true)
                    }

                    if (guides.isEmpty()) {
                        item {
                            NoResultsContent()
                        }
                    } else {
                        items(guides, key = { it.id }) { guide ->
                            GuideCard(
                                modifier = Modifier.padding(horizontal = SpaceSize.large),
                                guide = guide,
                                onClick = { onGuideClick(guide.id) },
                            )
                        }
                    }
                }

                GuidesUiState.Error -> item {
                    ErrorContent(onRetryClick = onRefresh)
                }
            }
        }
    }
}

@Composable
@Preview
fun GuidesScreenPreview() {
    val guides = listOf(
        Guide(
            id = "1",
            name = "Michelin Star Favorites",
            description = "The finest dining experiences in the city",
            numberOfRestaurant = 8,
            author = Author(id = "u1", name = "Sarah M."),
            visibility = GuideVisibility.PUBLIC
        ),
        Guide(
            id = "2",
            name = "Hidden Gems",
            description = "Undiscovered culinary treasures",
            numberOfRestaurant = 12,
            author = Author(id = "u2", name = "Michael R."),
            visibility = GuideVisibility.PUBLIC
        ),
        Guide(
            id = "3",
            name = "Best for Date Night",
            description = "Romantic ambiance and exceptional cuisine",
            numberOfRestaurant = 6,
            author = Author(id = "u3", name = "Ana P."),
            visibility = GuideVisibility.PUBLIC
        ),
    )
    AppTheme {
        GuidesScreenContent(
            state = GuidesUiState.Loaded(allGuides = guides, myGuides = guides.take(1), hasMore = true),
            isRefreshing = false,
            onRefresh = {},
        )
    }
}
