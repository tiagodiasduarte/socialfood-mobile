package pt.socialfood.presentation.authors

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
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

private const val LOAD_MORE_THRESHOLD = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorsScreen(
    viewModel: AuthorsViewModel = koinViewModel(),
    onAuthorClick: (String) -> Unit = {},
) {
    val authors = viewModel.authors.collectAsLazyPagingItems()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    AuthorsContent(
        authors = authors,
        state = state,
        isRefreshing = isRefreshing,
        searchQuery = searchQuery,
        onQueryChange = { viewModel.onSearchQueryChange(it) },
        onRefresh = {
            if (searchQuery.isBlank()) authors.refresh() else viewModel.refresh()
        },
        onLoadMore = { viewModel.loadMore() },
        onAuthorClick = onAuthorClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthorsContent(
    authors: LazyPagingItems<Author>,
    state: AuthorsUiState,
    isRefreshing: Boolean,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onAuthorClick: (String) -> Unit = {},
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
        if (!isBrowsing && reachedBottom && state is AuthorsUiState.Loaded && state.hasMore && !state.isLoadingMore) {
            onLoadMore()
        }
    }

    val isRefreshingResolved = if (isBrowsing) {
        authors.loadState.refresh is LoadState.Loading && authors.itemCount > 0
    } else {
        isRefreshing
    }

    PullToRefreshBox(
        isRefreshing = isRefreshingResolved,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().background(GreyBackground),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
            contentPadding = PaddingValues(bottom = SpaceSize.xxlarge),
        ) {
            item {
                AuthorsHeader(
                    searchQuery = searchQuery,
                    onQueryChange = { onQueryChange(it) },
                )
            }

            if (isBrowsing) {
                when {
                    authors.loadState.refresh is LoadState.Loading && authors.itemCount == 0 -> item {
                        AuthorsPlaceholder()
                    }

                    authors.loadState.refresh is LoadState.Error && authors.itemCount == 0 -> item {
                        ErrorContent(onRetryClick = { authors.retry() })
                    }

                    authors.itemCount == 0 -> item {
                        NoResultsContent(modifier = Modifier.padding(top = 100.dp))
                    }

                    else -> {
                        items(
                            count = authors.itemCount,
                            key = authors.itemKey { it.id },
                        ) { index ->
                            authors[index]?.let { author ->
                                AuthorCard(
                                    author = author,
                                    onAuthorClick = { onAuthorClick(author.id) },
                                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                                )
                            }
                        }

                        if (authors.loadState.append is LoadState.Loading) {
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
                    AuthorsUiState.Loading -> item {
                        AuthorsPlaceholder()
                    }

                    AuthorsUiState.Error -> item {
                        ErrorContent()
                    }

                    is AuthorsUiState.Loaded -> {
                        if (state.authors.isEmpty()) {
                            item {
                                NoResultsContent(modifier = Modifier.padding(top = 100.dp))
                            }
                        } else {
                            itemsIndexed(state.authors, key = { _, author -> author.id }) { _, author ->
                                AuthorCard(
                                    author = author,
                                    onAuthorClick = { onAuthorClick(author.id) },
                                    modifier = Modifier.padding(horizontal = SpaceSize.large),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AuthorsScreenBrowsePreview() {
    val sampleAuthors = listOf(
        Author(id = "1", name = "Sarah Mitchell"),
        Author(id = "2", name = "Michael Rodriguez"),
        Author(id = "3", name = "Emma Laurent"),
    )
    val authors = flowOf(PagingData.from(sampleAuthors)).collectAsLazyPagingItems()

    AppTheme {
        AuthorsContent(
            authors = authors,
            state = AuthorsUiState.Loading,
            isRefreshing = false,
            searchQuery = "",
            onQueryChange = {},
            onRefresh = {},
            onLoadMore = {},
            onAuthorClick = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AuthorsScreenSearchPreview() {
    val sampleAuthors = listOf(
        Author(id = "1", name = "Sarah Mitchell"),
        Author(id = "2", name = "Michael Rodriguez"),
        Author(id = "3", name = "Emma Laurent"),
    )
    val authors = flowOf(PagingData.empty<Author>()).collectAsLazyPagingItems()

    AppTheme {
        AuthorsContent(
            authors = authors,
            state = AuthorsUiState.Loaded(authors = sampleAuthors, hasMore = true),
            isRefreshing = false,
            searchQuery = "sarah",
            onQueryChange = {},
            onRefresh = {},
            onLoadMore = {},
            onAuthorClick = {},
        )
    }
}
