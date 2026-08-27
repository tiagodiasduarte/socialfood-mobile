package pt.socialfood.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.components.buttons.ActionButton
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.FavouriteRed
import pt.socialfood.ui.theme.ImagePlaceholderColor
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.back_button_description
import socialfood.composeapp.generated.resources.guide_detail_edit_button_description
import socialfood.composeapp.generated.resources.guide_detail_favourite_button_description
import socialfood.composeapp.generated.resources.guide_detail_share_button_description
import socialfood.composeapp.generated.resources.guide_edit_icon
import socialfood.composeapp.generated.resources.restaurant_detail_more_options_description
import socialfood.composeapp.generated.resources.share_icon

private val IconSize = 24.dp

@Suppress("LongMethod", "LongParameterList")
@Composable
fun TopActionButtons(
    showCloseButton: Boolean,
    onCloseClick: () -> Unit,
    showShareButton: Boolean,
    onShareClick: () -> Unit,
    showEditButton: Boolean,
    onEditClick: () -> Unit = {},
    showFavouriteButton: Boolean,
    isFavourite: Boolean,
    onToggleFavourite: () -> Unit = {},
    showMenuButton: Boolean,
    onMenuClick: () -> Unit = {},
    menuContent: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (showCloseButton) {
            ActionButton(
                modifier = Modifier.padding(SpaceSize.large),
                onClick = onCloseClick,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(Res.string.back_button_description),
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(IconSize),
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(SpaceSize.large),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            if (showShareButton) {
                ActionButton(onClick = onShareClick) {
                    Icon(
                        painter = painterResource(Res.drawable.share_icon),
                        tint = MaterialTheme.colorScheme.surface,
                        contentDescription = stringResource(Res.string.guide_detail_share_button_description),
                        modifier = Modifier.size(IconSize),
                    )
                }
            }

            if (showFavouriteButton) {
                ActionButton(onClick = onToggleFavourite) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        tint = if (isFavourite) FavouriteRed else MaterialTheme.colorScheme.surface,
                        contentDescription = stringResource(Res.string.guide_detail_favourite_button_description),
                        modifier = Modifier.size(IconSize),
                    )
                }
            }

            if (showEditButton) {
                ActionButton(onClick = onEditClick) {
                    Icon(
                        painter = painterResource(Res.drawable.guide_edit_icon),
                        contentDescription = stringResource(Res.string.guide_detail_edit_button_description),
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(IconSize),
                    )
                }
            }

            if (showMenuButton) {
                Box {
                    ActionButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(Res.string.restaurant_detail_more_options_description),
                            tint = MaterialTheme.colorScheme.surface,
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
@Preview
fun TopActionButtonsPreview() {
    AppTheme {
        Box(modifier = Modifier.fillMaxWidth().height(80.dp).background(ImagePlaceholderColor)) {
            TopActionButtons(
                showCloseButton = true,
                onCloseClick = {},
                showShareButton = true,
                onShareClick = {},
                showEditButton = true,
                onEditClick = {},
                showFavouriteButton = true,
                isFavourite = true,
                onToggleFavourite = {},
                showMenuButton = true,
            )
        }
    }
}
