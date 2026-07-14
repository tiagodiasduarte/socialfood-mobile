package pt.socialfood.presentation.authors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    AuthorsContent(
        state = state,
        isRefreshing = isRefreshing,
        searchQuery = searchQuery,
        onQueryChange = { viewModel.onSearchQueryChange(it) },
        onRefresh = { viewModel.refresh() },
        onLoadMore = { viewModel.loadMore() },
        onFollowClick = {},
        onAuthorClick = onAuthorClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthorsContent(
    state: AuthorsUiState,
    isRefreshing: Boolean,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onFollowClick: (Author) -> Unit,
    onAuthorClick: (String) -> Unit = {},
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
        if (reachedBottom && state is AuthorsUiState.Loaded && state.hasMore && !state.isLoadingMore) {
            onLoadMore()
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
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
                                onFollowClick = onFollowClick,
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AuthorsScreenPreview() {
    val sampleAuthors = listOf(
        Author(id = "1", name = "Sarah Mitchell", isFollowing = false),
        Author(id = "2", name = "Michael Rodriguez", isFollowing = true),
        Author(id = "3", name = "Emma Laurent", isFollowing = false),
    )
    AppTheme {
        AuthorsContent(
            state = AuthorsUiState.Loaded(authors = sampleAuthors, hasMore = true),
            isRefreshing = false,
            searchQuery = "",
            onQueryChange = {},
            onRefresh = {},
            onLoadMore = {},
            onFollowClick = {},
            onAuthorClick = {},
        )
    }
}
