package pt.socialfood.presentation.guide.shared.join

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.presentation.components.placeholder.GuideCardPlaceholder
import pt.socialfood.presentation.error.stringResource
import pt.socialfood.presentation.guide.shared.GuideBottomInfo
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.join_shared_guide_screen_close_button_description
import socialfood.composeapp.generated.resources.join_shared_guide_screen_join_button

@Composable
fun JoinSharedGuideCardDialog(state: JoinSharedGuideCardUiState, onJoinClick: () -> Unit, onCloseClick: () -> Unit) {
    Dialog(
        onDismissRequest = onCloseClick,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(SpaceSize.xlarge),
            contentAlignment = Alignment.Center,
        ) {
            JoinSharedGuideCard(
                state = state,
                onJoinClick = onJoinClick,
                onCloseClick = onCloseClick,
            )
        }
    }
}

@Composable
private fun JoinSharedGuideCard(
    state: JoinSharedGuideCardUiState,
    onJoinClick: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSize.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        JoinSharedGuideCardHeader(guide = state.guide, onCloseClick = onCloseClick)

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

            Spacer(Modifier.height(SpaceSize.small))

            GuideBottomInfo(guide = state.guide, fontColor = MaterialTheme.colorScheme.onBackground)

            Spacer(Modifier.height(SpaceSize.large))

            if (state.guide.description.isNotBlank()) {
                Text(
                    text = state.guide.description,
                    style = AppTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.height(SpaceSize.medium))

            if (state.joinErrorCode != null) {
                Text(
                    text = stringResource(state.joinErrorCode.stringResource()),
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(top = SpaceSize.small),
                )
            }

            Spacer(Modifier.height(SpaceSize.small))

            JoinSharedGuideJoinButton(state = state, onJoinClick = onJoinClick)
        }
    }
}

@Composable
private fun JoinSharedGuideCardHeader(guide: Guide, onCloseClick: () -> Unit) {
    Box {
        JoinSharedGuideCardImage(guide = guide)

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(SpaceSize.small)
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
private fun JoinSharedGuideJoinButton(state: JoinSharedGuideCardUiState, onJoinClick: () -> Unit) {
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
fun JoinSharedGuideCardPreview() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).padding(SpaceSize.xlarge),
            contentAlignment = Alignment.Center,
        ) {
            JoinSharedGuideCard(
                state = JoinSharedGuideCardUiState(
                    guide = Guide(
                        id = "g1",
                        name = "Best Brunch Spots",
                        description = "A curated list of the coziest brunch places in town",
                        visibility = GuideVisibility.SHARED,
                        author = Author(id = "a1", name = "Jane Doe", username = "janedoe"),
                        numberOfRestaurant = 12,
                    ),
                ),
                onJoinClick = {},
                onCloseClick = {},
            )
        }
    }
}

@Composable
@Preview
fun JoinSharedGuideCardErrorPreview() {
    AppTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).padding(SpaceSize.xlarge),
            contentAlignment = Alignment.Center,
        ) {
            JoinSharedGuideCard(
                state = JoinSharedGuideCardUiState(
                    guide = Guide(
                        id = "g1",
                        name = "Best Brunch Spots",
                        description = "A curated list of the coziest brunch places in town",
                        visibility = GuideVisibility.SHARED,
                        author = Author(id = "a1", name = "Jane Doe", username = "janedoe"),
                        numberOfRestaurant = 12,
                    ),
                    joinErrorCode = ErrorCode.GUIDE_NOT_FOUND,
                ),
                onJoinClick = {},
                onCloseClick = {},
            )
        }
    }
}
