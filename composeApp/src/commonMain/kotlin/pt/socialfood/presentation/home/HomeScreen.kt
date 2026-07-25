package pt.socialfood.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.home_list_see_all_label
import socialfood.composeapp.generated.resources.home_list_view_all_label
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionItem
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.User
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.presentation.guides.GuideCard
import pt.socialfood.presentation.restaurant.RestaurantCard
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

private val cardWidth = 300.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onGuideClick: (guideId: String) -> Unit = {},
    onRestaurantClick: (restaurantId: String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    HomeScreenContent(
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        onGuideClick = onGuideClick,
        onRestaurantClick = onRestaurantClick,
    )
}


@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    onGuideClick: (guideId: String) -> Unit = {},
    onRestaurantClick: (restaurantId: String) -> Unit = {},
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(GreyBackground),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.xlarge),
            contentPadding = PaddingValues(bottom = SpaceSize.xxlarge),
        ) {
            item {
                HomeHeader()
            }

            when (val current = state) {
                HomeUiState.Loading -> {
                    item {
                        HomePlaceholder()
                    }
                }

                is HomeUiState.Loaded -> {
                    if (current.sections.isEmpty()) {
                        item {
                            NoResultsContent()
                        }
                    } else {
                        items(current.sections, key = { it.id }) { section ->
                            HomeSectionRow(
                                section = section,
                                onGuideClick = onGuideClick,
                                onRestaurantClick = onRestaurantClick,
                            )
                        }
                    }
                }

                HomeUiState.Error -> {
                    item {
                        ErrorContent(onRetryClick = onRefresh)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSectionRow(
    section: HomeSection,
    onGuideClick: (guideId: String) -> Unit = {},
    onRestaurantClick: (restaurantId: String) -> Unit = {},
) {
    val sorted = section.items.sortedBy { it.position }
    val isGuideSection = sorted.any { it.itemType == HomeItemType.GUIDE }

    Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.xxlarge)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSize.large),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        if (sorted.isEmpty()) {
            Text(
                text = "No items yet",
                style = AppTypography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = SpaceSize.large),
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
                contentPadding = PaddingValues(horizontal = SpaceSize.large),
            ) {
                items(items = sorted, key = { it.id }) { item ->
                    HomeSectionItemCard(
                        item = item,
                        onGuideClick = onGuideClick,
                        onRestaurantClick = onRestaurantClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeSectionItemCard(
    item: HomeSectionItem,
    onGuideClick: (guideId: String) -> Unit = {},
    onRestaurantClick: (restaurantId: String) -> Unit = {},
) {
    when (item.itemType) {
        HomeItemType.RESTAURANT -> item.restaurant?.let {
            RestaurantCard(it, width = cardWidth, onClick = { onRestaurantClick(it.id) })
        }
        HomeItemType.GUIDE -> item.guide?.let {
            GuideCard(guide = it, width = cardWidth, onClick = { onGuideClick(it.id) })
        }
        HomeItemType.EVENT -> {}
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    val guides = listOf(
        Guide(
            id = "g1",
            name = "Michelin Star Favorites",
            description = "The finest dining experiences in the city",
            numberOfRestaurant = 8,
            author = Author(id = "u1", name = "Sarah M."),
            visibility = GuideVisibility.PUBLIC
        ),
        Guide(
            id = "g2",
            name = "Hidden Gems",
            description = "Undiscovered culinary treasures",
            numberOfRestaurant = 12,
            author = Author(id = "u2", name = "Michael R."),
            visibility = GuideVisibility.PUBLIC
        ),
    )
    val restaurants = listOf(
        Restaurant(
            id = "r1",
            name = "Le Jardin",
            description = "",
            city = "Midtown",
            country = "French",
            countryCode = "French",
            postalCode = "French",
            photoNames = emptyList(),
            address = "",
            rating = 4.8,
            userRatingCount = 320,
            websiteUrl = "",
            phoneNumber = "",
        ),
        Restaurant(
            id = "r2",
            name = "Sakura",
            description = "",
            city = "Midtown",
            country = "French",
            countryCode = "French",
            postalCode = "French",
            photoNames = emptyList(),
            address = "",
            rating = 4.6,
            userRatingCount = 210,
            websiteUrl = "",
            phoneNumber = "",
        ),
    )
    val sections = listOf(
        HomeSection(
            id = "s1",
            title = "Curated Guides",
            type = HomeSectionType.GUIDE_LIST,
            position = 0,
            isActive = true,
            items = guides.mapIndexed { index, guide ->
                HomeSectionItem(
                    id = "si_g$index",
                    sectionId = "s1",
                    itemId = guide.id,
                    itemType = HomeItemType.GUIDE,
                    position = index,
                    guide = guide,
                )
            },
        ),
        HomeSection(
            id = "s2",
            title = "Top Restaurants",
            type = HomeSectionType.RESTAURANT_LIST,
            position = 1,
            isActive = true,
            items = restaurants.mapIndexed { index, restaurant ->
                HomeSectionItem(
                    id = "si_r$index",
                    sectionId = "s2",
                    itemId = restaurant.id,
                    itemType = HomeItemType.RESTAURANT,
                    position = index,
                    restaurant = restaurant,
                )
            },
        ),
    )
    val state = HomeUiState.Loaded(
        sections = sections,
    )
    AppTheme {
        HomeScreenContent(
            state = state,
            isRefreshing = false,
            onRefresh = {}
        )
    }
}