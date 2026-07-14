package pt.socialfood.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.guide_detail_edit_button_description
import socialfood.composeapp.generated.resources.guide_edit_icon
import socialfood.composeapp.generated.resources.profile_favorites_guides_button
import socialfood.composeapp.generated.resources.profile_favorites_guides_button_description
import socialfood.composeapp.generated.resources.profile_favorites_restaurants_button
import socialfood.composeapp.generated.resources.profile_favorites_restaurants_button_description
import socialfood.composeapp.generated.resources.profile_guides_button
import socialfood.composeapp.generated.resources.profile_guides_button_description
import socialfood.composeapp.generated.resources.profile_logout_button
import socialfood.composeapp.generated.resources.profile_logout_button_description
import pt.socialfood.core.appVersion
import pt.socialfood.domain.model.User
import pt.socialfood.presentation.components.ActionButton
import pt.socialfood.presentation.components.NoResultsContent
import pt.socialfood.presentation.components.UserImage
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize

internal val HeaderHeight = 180.dp
internal val AvatarSize = 96.dp
internal val AvatarRingSize = 108.dp
internal val AvatarOverlap = AvatarRingSize / 2

@Composable
fun ProfileScreen(
    onEditProfileClick: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileContent(
        state = state,
        onLogoutClick = { viewModel.logout() },
        onEditProfileClick = onEditProfileClick,
    )
}

@Composable
private fun ProfileContent(
    state: ProfileUiState,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit = {},
) {
    when (state) {
        ProfileUiState.Loading -> ProfilePlaceholder()

        is ProfileUiState.Loaded -> UserContent(
            user = state.user,
            onLogoutClick = onLogoutClick,
            onEditProfileClick = onEditProfileClick,
        )

        ProfileUiState.Error -> NoResultsContent(modifier = Modifier.fillMaxSize())

        ProfileUiState.LoggedOut -> ProfilePlaceholder()
    }
}

@Composable
private fun UserContent(
    user: User,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreyBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeaderHeight + AvatarOverlap),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HeaderHeight)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF05A1A),
                                Color(0xFFB82010),
                            ),
                        ),
                    ),
            ) {
                ActionButton(
                    modifier = Modifier.padding(SpaceSize.large).align(Alignment.TopEnd),
                    onClick = { onEditProfileClick() }) {
                    Icon(
                        painter = painterResource(Res.drawable.guide_edit_icon),
                        contentDescription = stringResource(Res.string.guide_detail_edit_button_description),
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
            Box(
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Box(
                    modifier = Modifier
                        .size(AvatarRingSize)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    UserImage(
                        name = user.name,
                        imageUrl = user.imageUrl,
                        imageSize = AvatarSize
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSize.large)
                .padding(vertical = SpaceSize.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                user.bio?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            StatsRow()

            ContactCard(user = user)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
            ) {
                MenuRow(
                    icon = Icons.Outlined.MenuBook,
                    label = stringResource(Res.string.profile_guides_button),
                    contentDescription = stringResource(Res.string.profile_guides_button_description),
                )
                MenuRow(
                    icon = Icons.Outlined.FavoriteBorder,
                    label = stringResource(Res.string.profile_favorites_guides_button),
                    contentDescription = stringResource(Res.string.profile_favorites_guides_button_description),
                )
                MenuRow(
                    icon = Icons.Outlined.FavoriteBorder,
                    label = stringResource(Res.string.profile_favorites_restaurants_button),
                    contentDescription = stringResource(Res.string.profile_favorites_restaurants_button_description),
                )
                MenuRow(
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    label = stringResource(Res.string.profile_logout_button),
                    contentDescription = stringResource(Res.string.profile_logout_button_description),
                    color = MaterialTheme.colorScheme.primary,
                    hasChevron = false,
                    onClick = onLogoutClick
                )
            }

            Text(
                text = "v$appVersion",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
@Preview
fun ProfileScreenPreview() {
    val user = User(
        id = "u1",
        email = "john.doe@email.com",
        name = "John Doe",
    )
    AppTheme {
        ProfileContent(
            state = ProfileUiState.Loaded(user),
            onLogoutClick = {}
        )
    }
}
