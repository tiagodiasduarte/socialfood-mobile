package pt.socialfood.presentation.author.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import pt.socialfood.domain.model.User
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorsScreen(
    viewModel: AuthorsViewModel = koinViewModel(),
    onAuthorClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val authors = viewModel.authors.collectAsLazyPagingItems()
    val user by viewModel.user.collectAsStateWithLifecycle()

    AuthorsContent(
        authors = authors,
        user = user,
        onAuthorClick = onAuthorClick,
        onProfileClick = onProfileClick,
    )
}

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthorsContent(
    authors: LazyPagingItems<Author>,
    user: User? = null,
    onAuthorClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val listState = rememberLazyListState()

    val isRefreshing = authors.loadState.refresh is LoadState.Loading && authors.itemCount > 0

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { authors.refresh() },
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
                    userName = user?.name.orEmpty(),
                    userImageUrl = user?.imageUrl,
                    onProfileClick = onProfileClick,
                )
            }

            when (authors.loadState.refresh) {
                is LoadState.Loading if authors.itemCount == 0 -> item {
                    AuthorsPlaceholder()
                }

                is LoadState.Error if authors.itemCount == 0 -> item {
                    ErrorContent(onRetryClick = { authors.retry() })
                }

                is LoadState.NotLoading if authors.loadState.append.endOfPaginationReached &&
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AuthorsScreenPreview() {
    val sampleAuthors = listOf(
        Author(id = "1", name = "Sarah Mitchell", username = "sarahmitchell"),
        Author(id = "2", name = "Michael Rodriguez", username = "michaelrodriguez"),
        Author(id = "3", name = "Emma Laurent", username = "emmalaurent"),
    )
    val authors = flowOf(PagingData.from(sampleAuthors)).collectAsLazyPagingItems()

    AppTheme {
        AuthorsContent(
            authors = authors,
            onAuthorClick = {},
        )
    }
}
