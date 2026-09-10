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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.presentation.guide.GuidesScreenContent
import pt.socialfood.presentation.guide.SHARED_GUIDES_TAB
import pt.socialfood.presentation.guide.shared.join.JoinSharedGuideCardDialog
import pt.socialfood.presentation.guide.shared.join.JoinSharedGuideCodeInput
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
            onFavouriteClick = { viewModel.onToggleGuideFavourite(it) },
        )

        JoinGuideFab(onClick = { showJoinDialog = true })

        guideToJoin?.let { state ->
            JoinSharedGuideCardDialog(
                state = state,
                onJoinClick = { viewModel.onJoinGuideConfirm() },
                onCloseClick = { viewModel.onDismissJoinGuideCard() },
            )
        }
    }

    JoinSharedGuideCodeInput(
        show = showJoinDialog,
        state = joinGuideState,
        onConfirm = { code -> viewModel.onJoinGuide(code) },
        onDismiss = {
            showJoinDialog = false
            viewModel.onDismissJoinGuideError()
        },
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
