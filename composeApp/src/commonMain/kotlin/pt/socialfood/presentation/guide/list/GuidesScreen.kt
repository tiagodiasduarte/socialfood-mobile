package pt.socialfood.presentation.guide.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.flowOf
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
    val guides = viewModel.guides.collectAsLazyPagingItems()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    GuidesScreenContent(
        guides = guides,
        selectedTab = selectedTab,
        state = state,
        isRefreshing = isRefreshing,
        searchQuery = searchQuery,
        onTabSelected = { viewModel.onTabSelected(it) },
        onQueryChange = { viewModel.onSearchQueryChange(it) },
        onRefresh = {
            if (searchQuery.isBlank()) guides.refresh() else viewModel.refresh()
        },
        onLoadMore = { viewModel.loadMore() },
        onGuideClick = onGuideClick,
        onAddClick = onAddClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidesScreenContent(
    guides: LazyPagingItems<Guide>,
    selectedTab: Int = ALL_GUIDES_TAB,
    state: GuidesUiState = GuidesUiState.Loading,
    isRefreshing: Boolean = false,
    searchQuery: String = "",
    onTabSelected: (Int) -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onGuideClick: (guideId: String) -> Unit = {},
    onAddClick: () -> Unit = {},
) {
    val isBrowsing = searchQuery.isBlank()
    val listState = rememberLazyListState()

    val reachedBottom by remember(listState) {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisible != null && totalItems > 0 && lastVisible.index >= totalItems - 1 - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(reachedBottom, state, isBrowsing) {
        if (!isBrowsing && reachedBottom && state is GuidesUiState.Loaded && state.hasMore && !state.isLoadingMore) {
            onLoadMore()
        }
    }

    val isRefreshingResolved = if (isBrowsing) {
        guides.loadState.refresh is LoadState.Loading && guides.itemCount > 0
    } else {
        isRefreshing
    }

    PullToRefreshBox(
        isRefreshing = isRefreshingResolved,
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
                    onSelectedTab = onTabSelected,
                    onQueryChange = onQueryChange,
                    onAddClick = onAddClick,
                )
            }

            if (isBrowsing) {
                when {
                    guides.loadState.refresh is LoadState.Loading && guides.itemCount == 0 -> item {
                        GuidesPlaceholder()
                    }

                    guides.loadState.refresh is LoadState.Error && guides.itemCount == 0 -> item {
                        ErrorContent(onRetryClick = { guides.retry() })
                    }

                    guides.itemCount == 0 -> item {
                        NoResultsContent()
                    }

                    else -> {
                        items(
                            count = guides.itemCount,
                            key = guides.itemKey { it.id },
                        ) { index ->
                            guides[index]?.let { guide ->
                                GuideCard(
                                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                                    guide = guide,
                                    onClick = { onGuideClick(guide.id) },
                                )
                            }
                        }

                        if (guides.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceSize.large),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            } else {
                when (state) {
                    GuidesUiState.Loading -> item {
                        GuidesPlaceholder()
                    }

                    GuidesUiState.Error -> item {
                        ErrorContent()
                    }

                    is GuidesUiState.Loaded -> {
                        if (state.guides.isEmpty()) {
                            item {
                                NoResultsContent()
                            }
                        } else {
                            itemsIndexed(state.guides, key = { _, guide -> guide.id }) { _, guide ->
                                GuideCard(
                                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                                    guide = guide,
                                    onClick = { onGuideClick(guide.id) },
                                )
                            }

                            if (state.isLoadingMore) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(SpaceSize.large),
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
    }
}

private val previewGuides = listOf(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun GuidesScreenBrowsePreview() {
    val guides = flowOf(PagingData.from(previewGuides)).collectAsLazyPagingItems()

    AppTheme {
        GuidesScreenContent(
            guides = guides,
            selectedTab = ALL_GUIDES_TAB,
            state = GuidesUiState.Loading,
            searchQuery = "",
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun GuidesScreenSearchPreview() {
    val guides = flowOf(PagingData.empty<Guide>()).collectAsLazyPagingItems()

    AppTheme {
        GuidesScreenContent(
            guides = guides,
            selectedTab = ALL_GUIDES_TAB,
            state = GuidesUiState.Loaded(guides = previewGuides, hasMore = true),
            searchQuery = "hidden",
        )
    }
}
