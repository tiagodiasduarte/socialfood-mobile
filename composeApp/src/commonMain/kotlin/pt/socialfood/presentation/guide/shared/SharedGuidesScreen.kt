package pt.socialfood.presentation.guide.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.User
import pt.socialfood.presentation.guide.GuidesScreenContent
import pt.socialfood.presentation.guide.SHARED_GUIDES_TAB
import pt.socialfood.presentation.guide.shared.join.JoinSharedGuideCardDialog
import pt.socialfood.presentation.guide.shared.join.JoinSharedGuideCardUiState
import pt.socialfood.presentation.guide.shared.join.JoinSharedGuideCodeInput
import pt.socialfood.presentation.guide.shared.join.JoinSharedGuideDialogUiState
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.shared_guides_join_button

@Composable
fun SharedGuidesScreen(
    viewModel: SharedGuidesViewModel = koinViewModel(),
    selectedTab: Int = SHARED_GUIDES_TAB,
    onTabSelected: (Int) -> Unit = {},
    onGuideClick: (guideId: String) -> Unit = {},
    onAddClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onGuideJoined: (guideId: String) -> Unit = {},
) {
    val guides = viewModel.guides.collectAsLazyPagingItems()
    val favouriteGuideIds by viewModel.favouriteGuideIds.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()
    val joinGuideState by viewModel.joinGuideState.collectAsStateWithLifecycle()
    val guideToJoin by viewModel.guideToJoin.collectAsStateWithLifecycle()
    var showJoinDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(guideToJoin) {
        if (guideToJoin != null) showJoinDialog = false
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SharedGuidesViewModel.UiEvent.GuideJoined -> onGuideJoined(event.guideId)
            }
        }
    }

    SharedGuidesScreenContent(
        guides = guides,
        selectedTab = selectedTab,
        favouriteGuideIds = favouriteGuideIds,
        user = user,
        joinGuideState = joinGuideState,
        guideToJoin = guideToJoin,
        showJoinDialog = showJoinDialog,
        onTabSelected = onTabSelected,
        onGuideClick = onGuideClick,
        onAddClick = onAddClick,
        onProfileClick = onProfileClick,
        onFavouriteClick = { viewModel.onToggleGuideFavourite(it) },
        onJoinFabClick = { showJoinDialog = true },
        onJoinDialogConfirm = { viewModel.onJoinGuideConfirm() },
        onJoinDialogClose = { viewModel.onDismissJoinGuideCard() },
        onJoinCodeConfirm = { code -> viewModel.onJoinGuide(code) },
        onJoinCodeDismiss = {
            showJoinDialog = false
            viewModel.onDismissJoinGuideError()
        },
    )
}

@Composable
private fun SharedGuidesScreenContent(
    guides: LazyPagingItems<Guide>,
    selectedTab: Int = SHARED_GUIDES_TAB,
    favouriteGuideIds: Set<String> = emptySet(),
    user: User? = null,
    joinGuideState: JoinSharedGuideDialogUiState = JoinSharedGuideDialogUiState.Idle,
    guideToJoin: JoinSharedGuideCardUiState? = null,
    showJoinDialog: Boolean = false,
    onTabSelected: (Int) -> Unit = {},
    onGuideClick: (guideId: String) -> Unit = {},
    onAddClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFavouriteClick: (Guide) -> Unit = {},
    onJoinFabClick: () -> Unit = {},
    onJoinDialogConfirm: () -> Unit = {},
    onJoinDialogClose: () -> Unit = {},
    onJoinCodeConfirm: (String) -> Unit = {},
    onJoinCodeDismiss: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GuidesScreenContent(
            guides = guides,
            selectedTab = selectedTab,
            favouriteGuideIds = favouriteGuideIds,
            user = user,
            onTabSelected = onTabSelected,
            onGuideClick = onGuideClick,
            onAddClick = onAddClick,
            onProfileClick = onProfileClick,
            onFavouriteClick = onFavouriteClick,
        )

        JoinGuideFab(onClick = onJoinFabClick)

        guideToJoin?.let { state ->
            JoinSharedGuideCardDialog(
                state = state,
                onJoinClick = onJoinDialogConfirm,
                onCloseClick = onJoinDialogClose,
            )
        }
    }

    JoinSharedGuideCodeInput(
        show = showJoinDialog,
        state = joinGuideState,
        onConfirm = onJoinCodeConfirm,
        onDismiss = onJoinCodeDismiss,
    )
}

@Composable
private fun BoxScope.JoinGuideFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(SpaceSize.large),
    ) {
        Icon(
            imageVector = Icons.Default.Link,
            contentDescription = stringResource(Res.string.shared_guides_join_button),
        )
    }
}

@Composable
@Preview(heightDp = 700)
private fun SharedGuidesScreenPreview() {
    val loadedState = LoadState.NotLoading(endOfPaginationReached = true)
    val guideList = listOf(
        Guide(
            id = "1",
            name = "Weekend Road Trip Eats",
            description = "Shared with friends for our upcoming trip",
            numberOfRestaurant = 5,
            author = Author(id = "u1", name = "Sarah M.", username = "sarahm"),
            visibility = GuideVisibility.SHARED,
        ),
        Guide(
            id = "2",
            name = "Office Lunch Spots",
            description = "Team favorites near the office",
            numberOfRestaurant = 9,
            author = Author(id = "u2", name = "Michael R.", username = "michaelr"),
            visibility = GuideVisibility.SHARED,
        ),
        Guide(
            id = "3",
            name = "Best Brunch in Town",
            description = "Sunday brunch favorites",
            numberOfRestaurant = 6,
            author = Author(id = "u3", name = "Ana P.", username = "anap"),
            visibility = GuideVisibility.SHARED,
        ),
    )
    val guides = flowOf(
        PagingData.from(
            guideList,
            sourceLoadStates = LoadStates(refresh = loadedState, prepend = loadedState, append = loadedState),
        ),
    ).collectAsLazyPagingItems()

    AppTheme {
        SharedGuidesScreenContent(
            guides = guides,
            favouriteGuideIds = setOf("2"),
        )
    }
}

@Composable
@Preview(heightDp = 700)
private fun SharedGuidesScreenEmptyPreview() {
    val emptyLoadState = LoadState.NotLoading(endOfPaginationReached = true)
    val guides = flowOf(
        PagingData.empty<Guide>(
            sourceLoadStates = LoadStates(refresh = emptyLoadState, prepend = emptyLoadState, append = emptyLoadState),
        ),
    ).collectAsLazyPagingItems()

    AppTheme {
        SharedGuidesScreenContent(guides = guides)
    }
}
