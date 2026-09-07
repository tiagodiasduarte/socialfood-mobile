package pt.socialfood.presentation.guide.all

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.presentation.guide.ALL_GUIDES_TAB
import pt.socialfood.presentation.guide.GuidesScreenContent

@Composable
fun AllGuidesScreen(
    viewModel: AllGuidesViewModel = koinViewModel(),
    selectedTab: Int = ALL_GUIDES_TAB,
    onTabSelected: (Int) -> Unit = {},
    onGuideClick: (guideId: String) -> Unit = {},
    onAddClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val guides = viewModel.guides.collectAsLazyPagingItems()
    val favouriteGuideIds by viewModel.favouriteGuideIds.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()

    GuidesScreenContent(
        guides = guides,
        selectedTab = selectedTab,
        favouriteGuideIds = favouriteGuideIds,
        user = user,
        onTabSelected = onTabSelected,
        onGuideClick = onGuideClick,
        onAddClick = onAddClick,
        onProfileClick = onProfileClick,
        onFavouriteClick = { viewModel.onToggleGuideFavourite(it) },
    )
}
