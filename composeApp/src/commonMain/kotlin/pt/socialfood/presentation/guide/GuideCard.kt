package pt.socialfood.presentation.guide

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.presentation.components.cardImageScrim
import pt.socialfood.presentation.components.placeholder.GuideCardPlaceholder
import pt.socialfood.presentation.guide.shared.GuideBottomInfo
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.FavouriteRed
import pt.socialfood.ui.theme.PrivateBadge
import pt.socialfood.ui.theme.PublicBadge
import pt.socialfood.ui.theme.SharedBadge
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.guides_private_icon
import socialfood.composeapp.generated.resources.guides_public_icon
import socialfood.composeapp.generated.resources.join_shared_guide_screen_close_button_description
import socialfood.composeapp.generated.resources.share_icon

internal val CardHeight = 180.dp

@Composable
fun GuideCard(
    guide: Guide,
    width: Dp? = null,
    isFavourite: Boolean = false,
    onClick: () -> Unit = {},
    onFavouriteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = (if (width != null) modifier.width(width) else modifier.fillMaxWidth())
            .height(CardHeight)
            .clip(RoundedCornerShape(SpaceSize.large))
            .clickable(onClick = onClick),
    ) {
        GuideCardBackground(guide = guide)

        if (onFavouriteClick != null) {
            GuideCardFavouriteButton(
                isFavourite = isFavourite,
                onFavouriteClick = onFavouriteClick,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }

        GuideCardContent(
            guide = guide,
            modifier = Modifier.align(Alignment.BottomStart),
        )

        BadgeVisibility(
            guide = guide,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(SpaceSize.large),
        )
    }
}

@Composable
private fun GuideCardBackground(guide: Guide, modifier: Modifier = Modifier) {
    if (guide.imageUrl != null) {
        SubcomposeAsyncImage(
            model = guide.imageUrl,
            contentDescription = guide.name,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize(),
            loading = { GuideCardPlaceholder() },
            error = { GuideCardPlaceholder() },
        )
    } else {
        GuideCardPlaceholder()
    }

    Box(modifier = modifier.fillMaxSize().cardImageScrim())
}

@Composable
private fun GuideCardFavouriteButton(
    isFavourite: Boolean,
    onFavouriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(SpaceSize.medium)
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.9f))
            .clickable(onClick = onFavouriteClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Favourite",
            tint = if (isFavourite) FavouriteRed else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun GuideCardContent(guide: Guide, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.small),
    ) {
        Text(
            text = guide.name,
            style = AppTypography.headlineMedium,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (guide.description.isNotBlank()) {
            Text(
                text = guide.description,
                style = AppTypography.headlineSmall,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.height(SpaceSize.small))

        GuideBottomInfo(guide)
    }
}

@Composable
private fun BadgeVisibility(guide: Guide, modifier: Modifier) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(guide.visibility.badgeBackgroundColor()),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            painter = guide.visibility.badgeIcon(),
            contentDescription = stringResource(Res.string.join_shared_guide_screen_close_button_description),
            tint = Color.White,
        )
    }
}

@Composable
private fun GuideVisibility.badgeIcon(): Painter = when (this) {
    GuideVisibility.PUBLIC -> painterResource(Res.drawable.guides_public_icon)
    GuideVisibility.PRIVATE -> painterResource(Res.drawable.guides_private_icon)
    GuideVisibility.SHARED -> painterResource(Res.drawable.share_icon)
}

@Composable
private fun GuideVisibility.badgeBackgroundColor(): Color = when (this) {
    GuideVisibility.PUBLIC -> PublicBadge
    GuideVisibility.PRIVATE -> PrivateBadge
    GuideVisibility.SHARED -> SharedBadge
}

@Composable
@Preview
fun GuideCardPreview() {
    AppTheme {
        GuideCard(
            guide = Guide(
                id = "g1",
                name = "Best Brunch Spots",
                description = "A curated list of the coziest brunch places in town",
                visibility = GuideVisibility.PUBLIC,
                author = Author(id = "a1", name = "Jane Doe", username = "janedoe"),
                numberOfRestaurant = 12,
            ),
            isFavourite = true,
            onFavouriteClick = {},
        )
    }
}
