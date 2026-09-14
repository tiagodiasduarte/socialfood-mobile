package pt.socialfood.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.components.buttons.ActionButton
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.FavouriteRed
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.back_button_description
import socialfood.composeapp.generated.resources.create_guide_save_button
import socialfood.composeapp.generated.resources.guide_detail_edit_button_description
import socialfood.composeapp.generated.resources.guide_detail_favourite_button_description
import socialfood.composeapp.generated.resources.guide_detail_share_button_description
import socialfood.composeapp.generated.resources.guide_edit_icon
import socialfood.composeapp.generated.resources.guides_add_button_description
import socialfood.composeapp.generated.resources.restaurant_detail_more_options_description
import socialfood.composeapp.generated.resources.share_icon

private val IconSize = 24.dp
private val DefaultHeight = 56.dp

@Suppress("LongMethod", "LongParameterList")
@Composable
fun TopActionBar(
    showCloseButton: Boolean = false,
    onCloseClick: (() -> Unit)? = null,
    showShareButton: Boolean = false,
    onShareClick: (() -> Unit)? = null,
    showEditButton: Boolean = false,
    onEditClick: (() -> Unit)? = null,
    showFavouriteButton: Boolean = false,
    isFavourite: Boolean = false,
    onToggleFavourite: (() -> Unit)? = null,
    showMenuButton: Boolean = false,
    onMenuClick: (() -> Unit)? = null,
    menuContent: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    height: Dp = DefaultHeight,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(horizontal = SpaceSize.large),
    ) {
        if (showCloseButton) {
            ActionButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = onCloseClick ?: {},
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(Res.string.back_button_description),
                    tint = iconTint,
                    modifier = Modifier.size(IconSize),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            if (showShareButton) {
                ActionButton(onClick = onShareClick ?: {}) {
                    Icon(
                        painter = painterResource(Res.drawable.share_icon),
                        tint = iconTint,
                        contentDescription = stringResource(Res.string.guide_detail_share_button_description),
                        modifier = Modifier.size(IconSize),
                    )
                }
            }

            if (showFavouriteButton) {
                ActionButton(onClick = onToggleFavourite ?: {}) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        tint = if (isFavourite) FavouriteRed else iconTint,
                        contentDescription = stringResource(Res.string.guide_detail_favourite_button_description),
                        modifier = Modifier.size(IconSize),
                    )
                }
            }

            if (showEditButton) {
                ActionButton(onClick = onEditClick ?: {}) {
                    Icon(
                        painter = painterResource(Res.drawable.guide_edit_icon),
                        contentDescription = stringResource(Res.string.guide_detail_edit_button_description),
                        tint = iconTint,
                        modifier = Modifier.size(IconSize),
                    )
                }
            }

            if (showMenuButton) {
                Box {
                    ActionButton(onClick = onMenuClick ?: {}) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(Res.string.restaurant_detail_more_options_description),
                            tint = iconTint,
                            modifier = Modifier.size(IconSize),
                        )
                    }
                    menuContent()
                }
            }
        }
    }
}

@Composable
fun TopActionBar(
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    userImageUrl: String? = null,
    onProfileClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    actionButton: ActionButton = ActionButton.None,
    isActionLoading: Boolean = false,
    onActionClick: () -> Unit = {},
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    height: Dp = DefaultHeight,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = SpaceSize.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (userImageUrl != null && onProfileClick != null) {
            UserImage(
                imageUrl = userImageUrl,
                imageSize = 32.dp,
                modifier = Modifier.clickable(onClick = onProfileClick),
            )
        } else {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(Res.string.back_button_description),
                        tint = iconTint,
                    )
                }
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = titleColor,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )

        when (actionButton) {
            ActionButton.Save -> SaveActionButton(isLoading = isActionLoading, onClick = onActionClick)
            ActionButton.Add -> AddActionButton(iconTint = iconTint, onClick = onActionClick)
            ActionButton.None -> {}
        }
    }
}

@Composable
private fun SaveActionButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(SpaceSize.large),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(SpaceSize.large),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Text(
                text = stringResource(Res.string.create_guide_save_button),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun AddActionButton(iconTint: Color, onClick: () -> Unit) {
    Icon(
        imageVector = Icons.Filled.Add,
        contentDescription = stringResource(Res.string.guides_add_button_description),
        tint = iconTint,
        modifier = Modifier
            .size(30.dp)
            .clickable(onClick = onClick),
    )
}

@Composable
@Preview
fun TopActionBarPreview() {
    AppTheme {
        TopActionBar(
            showCloseButton = true,
            showShareButton = true,
            showEditButton = true,
            showFavouriteButton = true,
            isFavourite = true,
            showMenuButton = true,
        )
    }
}

@Preview
@Composable
private fun TopActionBarWithActionAddPreview() {
    AppTheme {
        TopActionBar(
            title = "Edit Profile",
            onBackClick = {},
            actionButton = ActionButton.Add,
            onActionClick = {},
        )
    }
}

@Preview
@Composable
private fun TopActionBarWithActionSavePreview() {
    AppTheme {
        TopActionBar(
            title = "Edit Profile",
            onBackClick = {},
            actionButton = ActionButton.Save,
            onActionClick = {},
        )
    }
}

@Preview
@Composable
private fun TopActionBarWithProfileAndAddPreview() {
    AppTheme {
        TopActionBar(
            title = "Guides",
            titleColor = MaterialTheme.colorScheme.primary,
            userImageUrl = "https://image",
            onProfileClick = {},
            actionButton = ActionButton.Add,
            onActionClick = {},
            iconTint = MaterialTheme.colorScheme.primary,
        )
    }
}

enum class ActionButton {
    Add,
    Save,
    None,
}
