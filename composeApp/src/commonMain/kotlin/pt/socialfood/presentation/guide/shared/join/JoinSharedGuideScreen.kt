package pt.socialfood.presentation.guide.shared.join

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.placeholder.GuideCardPlaceholder
import pt.socialfood.presentation.error.stringResource
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.join_shared_guide_screen_close_button_description
import socialfood.composeapp.generated.resources.join_shared_guide_screen_join_button
import socialfood.composeapp.generated.resources.join_shared_guide_screen_restaurants_count_label

@Composable
fun JoinSharedGuideScreen(
    guideId: String,
    viewModel: JoinSharedGuideViewModel = koinViewModel { parametersOf(guideId) },
    onCloseClick: () -> Unit = {},
    onJoined: (guideId: String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is JoinSharedGuideViewModel.UiEvent.Joined -> onJoined(event.guideId)
            }
        }
    }

    JoinSharedGuideContent(
        state = state,
        onCloseClick = onCloseClick,
        onJoinClick = viewModel::onJoinClick,
        onRetryClick = viewModel::load,
    )
}

@Composable
private fun JoinSharedGuideContent(
    state: JoinSharedGuideScreenUiState,
    onCloseClick: () -> Unit,
    onJoinClick: () -> Unit,
    onRetryClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is JoinSharedGuideScreenUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
            )

            is JoinSharedGuideScreenUiState.Error -> ErrorContent(
                modifier = Modifier.align(Alignment.Center),
                backgroundColor = Color.Transparent,
                onRetryClick = onRetryClick,
            )

            is JoinSharedGuideScreenUiState.Loaded -> JoinSharedGuideCard(
                state = state,
                onJoinClick = onJoinClick,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(SpaceSize.xlarge),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(SpaceSize.large)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(onClick = onCloseClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.join_shared_guide_screen_close_button_description),
                tint = Color.White,
            )
        }
    }
}

@Composable
private fun JoinSharedGuideCard(
    state: JoinSharedGuideScreenUiState.Loaded,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSize.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        JoinSharedGuideCardImage(guide = state.guide)

        Column(
            modifier = Modifier.padding(SpaceSize.large),
            verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
        ) {
            Text(
                text = state.guide.name,
                style = AppTypography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            if (state.guide.description.isNotBlank()) {
                Text(
                    text = state.guide.description,
                    style = AppTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = stringResource(
                    Res.string.join_shared_guide_screen_restaurants_count_label,
                    state.guide.numberOfRestaurant,
                ),
                style = AppTypography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            JoinSharedGuideJoinButton(state = state, onJoinClick = onJoinClick)
        }
    }
}

@Composable
private fun JoinSharedGuideJoinButton(state: JoinSharedGuideScreenUiState.Loaded, onJoinClick: () -> Unit) {
    Button(
        onClick = onJoinClick,
        enabled = !state.isJoining,
        shape = RoundedCornerShape(SpaceSize.large),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = Modifier.fillMaxWidth().padding(top = SpaceSize.small),
    ) {
        if (state.isJoining) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Text(stringResource(Res.string.join_shared_guide_screen_join_button))
        }
    }

    if (state.joinErrorCode != null) {
        Text(
            text = stringResource(state.joinErrorCode.stringResource()),
            style = AppTypography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = SpaceSize.small),
        )
    }
}

@Composable
private fun JoinSharedGuideCardImage(guide: Guide) {
    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        if (guide.imageUrl != null) {
            SubcomposeAsyncImage(
                model = guide.imageUrl,
                contentDescription = guide.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = { GuideCardPlaceholder() },
                error = { GuideCardPlaceholder() },
            )
        } else {
            GuideCardPlaceholder()
        }
    }
}

@Composable
@Preview
fun JoinSharedGuideScreenPreview() {
    AppTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f))) {
            JoinSharedGuideContent(
                state = JoinSharedGuideScreenUiState.Loaded(
                    guide = Guide(
                        id = "g1",
                        name = "Best Brunch Spots",
                        description = "A curated list of the coziest brunch places in town",
                        visibility = GuideVisibility.SHARED,
                        author = Author(id = "a1", name = "Jane Doe", username = "janedoe"),
                        numberOfRestaurant = 12,
                    ),
                ),
                onCloseClick = {},
                onJoinClick = {},
                onRetryClick = {},
            )
        }
    }
}
