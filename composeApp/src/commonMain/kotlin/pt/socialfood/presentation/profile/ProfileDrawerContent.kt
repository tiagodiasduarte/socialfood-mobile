package pt.socialfood.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pt.socialfood.core.appBuildDate
import pt.socialfood.core.appVersion
import pt.socialfood.domain.model.User
import pt.socialfood.presentation.components.UserImage
import pt.socialfood.presentation.components.buttons.ActionButton
import pt.socialfood.presentation.theme.ThemeBottomSheet
import pt.socialfood.presentation.theme.isThemeModeSelectionSupported
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.ProfileGradientEnd
import pt.socialfood.ui.theme.ProfileGradientStart
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.guide_detail_edit_button_description
import socialfood.composeapp.generated.resources.guide_edit_icon
import socialfood.composeapp.generated.resources.guides_icon
import socialfood.composeapp.generated.resources.logout_icon
import socialfood.composeapp.generated.resources.profile_favorite_guides_button
import socialfood.composeapp.generated.resources.profile_favorite_guides_button_description
import socialfood.composeapp.generated.resources.profile_favorites_restaurants_button
import socialfood.composeapp.generated.resources.profile_favorites_restaurants_button_description
import socialfood.composeapp.generated.resources.profile_icon
import socialfood.composeapp.generated.resources.profile_logout_button
import socialfood.composeapp.generated.resources.profile_logout_button_description
import socialfood.composeapp.generated.resources.profile_profile_button
import socialfood.composeapp.generated.resources.profile_profile_button_description
import socialfood.composeapp.generated.resources.profile_stat_followers_label
import socialfood.composeapp.generated.resources.profile_stat_following_label
import socialfood.composeapp.generated.resources.profile_stat_guides_label
import socialfood.composeapp.generated.resources.profile_theme_button
import socialfood.composeapp.generated.resources.profile_theme_button_description
import socialfood.composeapp.generated.resources.profile_visited_restaurants_button
import socialfood.composeapp.generated.resources.profile_visited_restaurants_button_description
import socialfood.composeapp.generated.resources.profile_wish_restaurants_button
import socialfood.composeapp.generated.resources.profile_wish_restaurants_button_description
import socialfood.composeapp.generated.resources.restaurants_icon
import socialfood.composeapp.generated.resources.theme_icon
import socialfood.composeapp.generated.resources.visited_icon
import socialfood.composeapp.generated.resources.wish_icon

private val DrawerAvatarSize = 44.dp
private val DrawerAvatarRingSize = 48.dp

@Composable
fun ProfileDrawerContent(
    viewModel: ProfileViewModel = koinViewModel(),
    onProfileClick: (authorId: String) -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onFavouriteGuidesClick: () -> Unit = {},
    onFavouriteRestaurantsClick: () -> Unit = {},
    onWishRestaurantsClick: () -> Unit = {},
    onVisitedRestaurantsClick: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showThemeSheet by remember { mutableStateOf(false) }

    ProfileDrawerSheet(
        state = state,
        onLogoutClick = { viewModel.logout() },
        onProfileClick = onProfileClick,
        onEditProfileClick = onEditProfileClick,
        onFavouriteGuidesClick = onFavouriteGuidesClick,
        onFavouriteRestaurantsClick = onFavouriteRestaurantsClick,
        onWishRestaurantsClick = onWishRestaurantsClick,
        onVisitedRestaurantsClick = onVisitedRestaurantsClick,
        onThemeClick = { showThemeSheet = true },
    )

    if (showThemeSheet) {
        ThemeBottomSheet(onDismiss = { showThemeSheet = false })
    }
}

@Composable
private fun ProfileDrawerSheet(
    state: ProfileUiState,
    onLogoutClick: () -> Unit,
    onProfileClick: (authorId: String) -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onFavouriteGuidesClick: () -> Unit = {},
    onFavouriteRestaurantsClick: () -> Unit = {},
    onWishRestaurantsClick: () -> Unit = {},
    onVisitedRestaurantsClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
) {
    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface, windowInsets = WindowInsets(0.dp)) {
        when (state) {
            is ProfileUiState.Loaded -> DrawerUserContent(
                user = state.user,
                onLogoutClick = onLogoutClick,
                onProfileClick = onProfileClick,
                onEditProfileClick = onEditProfileClick,
                onFavouriteGuidesClick = onFavouriteGuidesClick,
                onFavouriteRestaurantsClick = onFavouriteRestaurantsClick,
                onWishRestaurantsClick = onWishRestaurantsClick,
                onVisitedRestaurantsClick = onVisitedRestaurantsClick,
                onThemeClick = onThemeClick,
            )

            ProfileUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(SpaceSize.xxlarge),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is ProfileUiState.Error, ProfileUiState.LoggedOut -> Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun DrawerUserContent(
    user: User,
    onLogoutClick: () -> Unit,
    onProfileClick: (authorId: String) -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onFavouriteGuidesClick: () -> Unit = {},
    onFavouriteRestaurantsClick: () -> Unit = {},
    onWishRestaurantsClick: () -> Unit = {},
    onVisitedRestaurantsClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
) {
    Column(modifier = Modifier.fillMaxSize()) {
        DrawerHeader(user = user, onEditProfileClick = onEditProfileClick)

        Column(
            modifier = Modifier.padding(vertical = SpaceSize.medium),
        ) {
            DrawerMenuRow(
                icon = Res.drawable.profile_icon,
                label = stringResource(Res.string.profile_profile_button),
                contentDescription = stringResource(Res.string.profile_profile_button_description),
                onClick = { onProfileClick(user.id) },
            )
            DrawerMenuRow(
                icon = Res.drawable.guides_icon,
                label = stringResource(Res.string.profile_favorite_guides_button),
                contentDescription = stringResource(Res.string.profile_favorite_guides_button_description),
                onClick = onFavouriteGuidesClick,
            )
            DrawerMenuRow(
                icon = Res.drawable.restaurants_icon,
                label = stringResource(Res.string.profile_favorites_restaurants_button),
                contentDescription = stringResource(Res.string.profile_favorites_restaurants_button_description),
                onClick = onFavouriteRestaurantsClick,
            )
            DrawerMenuRow(
                icon = Res.drawable.wish_icon,
                label = stringResource(Res.string.profile_wish_restaurants_button),
                contentDescription = stringResource(Res.string.profile_wish_restaurants_button_description),
                onClick = onWishRestaurantsClick,
            )
            DrawerMenuRow(
                icon = Res.drawable.visited_icon,
                label = stringResource(Res.string.profile_visited_restaurants_button),
                contentDescription = stringResource(Res.string.profile_visited_restaurants_button_description),
                onClick = onVisitedRestaurantsClick,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()

        DrawerBottomMenu(onThemeClick = onThemeClick, onLogoutClick = onLogoutClick)

        Text(
            text = if (appBuildDate.isNotBlank()) "v$appVersion ($appBuildDate)" else "v$appVersion",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = SpaceSize.large),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DrawerBottomMenu(onThemeClick: () -> Unit, onLogoutClick: () -> Unit) {
    Column(modifier = Modifier.navigationBarsPadding().padding(vertical = SpaceSize.small)) {
        if (isThemeModeSelectionSupported) {
            DrawerMenuRow(
                icon = Res.drawable.theme_icon,
                label = stringResource(Res.string.profile_theme_button),
                contentDescription = stringResource(Res.string.profile_theme_button_description),
                onClick = onThemeClick,
            )
        }
        DrawerMenuRow(
            icon = Res.drawable.logout_icon,
            label = stringResource(Res.string.profile_logout_button),
            contentDescription = stringResource(Res.string.profile_logout_button_description),
            color = MaterialTheme.colorScheme.primary,
            onClick = onLogoutClick,
        )
    }
}

@Composable
private fun DrawerHeader(user: User, onEditProfileClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(colors = listOf(ProfileGradientStart, ProfileGradientEnd)),
            )
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(SpaceSize.large),
    ) {
        ActionButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onEditProfileClick,
        ) {
            Icon(
                painter = painterResource(Res.drawable.guide_edit_icon),
                contentDescription = stringResource(Res.string.guide_detail_edit_button_description),
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.medium)) {
            Box(
                modifier = Modifier
                    .size(DrawerAvatarRingSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                UserImage(name = user.name, imageUrl = user.imageUrl, imageSize = DrawerAvatarSize)
            }

            Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.small)) {
                Text(
                    text = user.name,
                    style = AppTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
                Text(
                    text = "@${user.username}",
                    style = AppTypography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }

            Spacer(Modifier.height(SpaceSize.small))

            DrawerStatsRow()
        }
    }
}

@Composable
private fun DrawerStatsRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(SpaceSize.large)) {
        DrawerStatItem(
            value = "13",
            label = stringResource(Res.string.profile_stat_guides_label),
        )
        DrawerStatItem(
            value = "5555",
            label = stringResource(Res.string.profile_stat_followers_label),
        )
        DrawerStatItem(
            value = "8463",
            label = stringResource(Res.string.profile_stat_following_label),
        )
    }
}

@Composable
private fun DrawerStatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(text = value, style = AppTypography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Spacer(Modifier.width(SpaceSize.small))
        Text(text = label, style = AppTypography.bodySmall, color = Color.White.copy(alpha = 0.85f))
    }
}

@Composable
private fun DrawerMenuRow(
    icon: DrawableResource,
    label: String,
    contentDescription: String,
    color: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(SpaceSize.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size(22.dp),
        )
        Text(text = label, style = AppTypography.labelMedium, color = color)
    }
}

@Composable
@Preview
private fun ProfileDrawerContentPreview() {
    val user = User(
        id = "u1",
        email = "john.doe@email.com",
        name = "John Doe",
        username = "johndoe",
    )
    AppTheme {
        ProfileDrawerSheet(
            state = ProfileUiState.Loaded(user),
            onLogoutClick = {},
        )
    }
}
