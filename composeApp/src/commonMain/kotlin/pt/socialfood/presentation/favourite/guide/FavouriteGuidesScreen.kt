package pt.socialfood.presentation.favourite.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.presentation.components.PullToRefreshContent
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.back_button_description
import socialfood.composeapp.generated.resources.favourites_guides_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteGuidesScreen(
    onBackClick: () -> Unit,
    onGuideClick: (guideId: String) -> Unit = {},
    viewModel: FavouriteGuidesViewModel = koinViewModel(),
) {
    val guides = viewModel.guides.collectAsLazyPagingItems()

    FavouriteGuidesContent(
        guides = guides,
        onBackClick = onBackClick,
        onGuideClick = onGuideClick,
        onRemoveClick = viewModel::removeFavourite,
    )
}

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavouriteGuidesContent(
    guides: LazyPagingItems<Guide>,
    onBackClick: () -> Unit,
    onGuideClick: (guideId: String) -> Unit = {},
    onRemoveClick: (guideId: String) -> Unit = {},
) {
    val listState = rememberLazyListState()
    val isRefreshing = guides.loadState.refresh is LoadState.Loading && guides.itemCount > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        TopBar(onBackClick = onBackClick)

        when {
            guides.loadState.refresh is LoadState.Loading && guides.itemCount == 0 ->
                FavouriteGuidesSkeleton(modifier = Modifier.fillMaxSize())

            guides.loadState.refresh is LoadState.Error && guides.itemCount == 0 -> ErrorContent(
                modifier = Modifier.fillMaxSize(),
                onRetryClick = { guides.retry() },
            )

            guides.loadState.refresh is LoadState.NotLoading &&
                guides.loadState.append.endOfPaginationReached &&
                guides.itemCount == 0 -> NoResultsContent(modifier = Modifier.fillMaxSize())

            else -> PullToRefreshContent(
                isRefreshing = isRefreshing,
                onRefresh = { guides.refresh() },
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
                    items(count = guides.itemCount, key = guides.itemKey { it.id }) { index ->
                        guides[index]?.let { guide ->
                            FavoriteGuideCard(
                                guide = guide,
                                onClick = { onGuideClick(guide.id) },
                                onRemoveClick = { onRemoveClick(guide.id) },
                            )
                        }
                    }

                    if (guides.loadState.append is LoadState.Loading) {
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
private fun TopBar(onBackClick: () -> Unit) {
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
    val items = flowOf(PagingData.from(guides)).collectAsLazyPagingItems()

    AppTheme {
        FavouriteGuidesContent(
            guides = items,
            onBackClick = {},
        )
    }
}

@Preview
@Composable
private fun FavouriteGuidesScreenEmptyPreview() {
    val emptyLoadState = LoadState.NotLoading(endOfPaginationReached = true)
    val items = flowOf(
        PagingData.empty<Guide>(
            sourceLoadStates = LoadStates(refresh = emptyLoadState, prepend = emptyLoadState, append = emptyLoadState),
        ),
    ).collectAsLazyPagingItems()

    AppTheme {
        FavouriteGuidesContent(
            guides = items,
            onBackClick = {},
        )
    }
}
