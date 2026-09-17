package pt.socialfood.presentation.guide

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.presentation.components.FavouriteButton
import pt.socialfood.presentation.components.cardImageScrim
import pt.socialfood.presentation.components.placeholder.GuideCardPlaceholder
import pt.socialfood.presentation.guide.extensions.badgeBackgroundColor
import pt.socialfood.presentation.guide.extensions.badgeIcon
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.join_shared_guide_screen_close_button_description

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
    Card(
        modifier = (if (width != null) modifier.width(width) else modifier.fillMaxWidth())
            .height(CardHeight),
        shape = RoundedCornerShape(SpaceSize.large),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Box {
            GuideCardBackground(guide = guide)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSize.large),
            ) {
                if (onFavouriteClick != null) {
                    FavouriteButton(
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
                    modifier = Modifier.align(Alignment.TopStart),
                )
            }
        }
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
private fun GuideCardContent(guide: Guide, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
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

        GuideAndAuthorInfo(guide)
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
