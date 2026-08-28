package pt.socialfood.presentation.guide

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.User
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.presentation.components.PullToRefreshContent
import pt.socialfood.presentation.guide.all.AllGuidesScreen
import pt.socialfood.presentation.guide.my.MyGuidesScreen
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

const val ALL_GUIDES_TAB = 0
const val MY_GUIDES_TAB = 1

@Composable
fun GuidesScreen(
    onGuideClick: (guideId: String) -> Unit = {},
    onAddClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableStateOf(ALL_GUIDES_TAB) }

    when (selectedTab) {
        MY_GUIDES_TAB -> MyGuidesScreen(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onGuideClick = onGuideClick,
            onAddClick = onAddClick,
            onProfileClick = onProfileClick,
        )

        else -> AllGuidesScreen(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onGuideClick = onGuideClick,
            onAddClick = onAddClick,
            onProfileClick = onProfileClick,
        )
    }
}

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidesScreenContent(
    guides: LazyPagingItems<Guide>,
    selectedTab: Int = ALL_GUIDES_TAB,
    favouriteGuideIds: Set<String> = emptySet(),
    user: User? = null,
    onTabSelected: (Int) -> Unit = {},
    onGuideClick: (guideId: String) -> Unit = {},
    onAddClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFavouriteClick: (Guide) -> Unit = {},
) {
    val listState = rememberLazyListState()

    val isRefreshing = guides.loadState.refresh is LoadState.Loading && guides.itemCount > 0

    PullToRefreshContent(
        isRefreshing = isRefreshing,
        onRefresh = { guides.refresh() },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
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
                    onSelectedTab = onTabSelected,
                    onAddClick = onAddClick,
                    userImageUrl = user?.imageUrl,
                    onProfileClick = onProfileClick,
                )
            }

            when (guides.loadState.refresh) {
                is LoadState.Loading if guides.itemCount == 0 -> item {
                    GuidesSkeleton()
                }

                is LoadState.Error if guides.itemCount == 0 -> item {
                    ErrorContent(onRetryClick = { guides.retry() })
                }

                is LoadState.NotLoading if guides.loadState.append.endOfPaginationReached &&
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
                                isFavourite = guide.id in favouriteGuideIds,
                                onClick = { onGuideClick(guide.id) },
                                onFavouriteClick = { onFavouriteClick(guide) },
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
        }
    }
}

@Composable
@Preview
fun GuidesScreenPreview() {
    val guideList = listOf(
        Guide(
            id = "1",
            name = "Michelin Star Favorites",
            description = "The finest dining experiences in the city",
            numberOfRestaurant = 8,
            author = Author(id = "u1", name = "Sarah M.", username = "sarahm"),
            visibility = GuideVisibility.PUBLIC,
        ),
        Guide(
            id = "2",
            name = "Hidden Gems",
            description = "Undiscovered culinary treasures",
            numberOfRestaurant = 12,
            author = Author(id = "u2", name = "Michael R.", username = "michaelr"),
            visibility = GuideVisibility.PUBLIC,
        ),
        Guide(
            id = "3",
            name = "Best for Date Night",
            description = "Romantic ambiance and exceptional cuisine",
            numberOfRestaurant = 6,
            author = Author(id = "u3", name = "Ana P.", username = "anap"),
            visibility = GuideVisibility.PUBLIC,
        ),
    )
    val guides = flowOf(PagingData.from(guideList)).collectAsLazyPagingItems()

    AppTheme {
        GuidesScreenContent(
            guides = guides,
            selectedTab = ALL_GUIDES_TAB,
            favouriteGuideIds = setOf("2"),
        )
    }
}
