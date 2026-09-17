package pt.socialfood.presentation.favourite.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.presentation.components.buttons.OutlinedButton
import pt.socialfood.presentation.guide.GuideAndAuthorInfo
import pt.socialfood.presentation.guide.extensions.badgeBackgroundColor
import pt.socialfood.presentation.guide.extensions.badgeContentDescription
import pt.socialfood.presentation.guide.extensions.badgeIcon
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.favorite_card_remove_button

internal val CardHeight = 183.dp

@Composable
fun FavoriteGuideCard(
    guide: Guide,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onRemoveClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .height(CardHeight)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(SpaceSize.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSize.large, vertical = SpaceSize.large),
        ) {
            FavoriteGuideCardInfoRow(guide)

            Spacer(Modifier.height(SpaceSize.large))

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.favorite_card_remove_button),
                icon = Icons.Outlined.Delete,
                onClick = onRemoveClick,
            )
        }
    }
}

@Composable
private fun FavoriteGuideCardInfoRow(guide: Guide) {
    Box {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            Box(
                modifier = Modifier
                    .size(95.dp)
                    .clip(RoundedCornerShape(SpaceSize.medium))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                guide.imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = guide.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpaceSize.large),
            ) {
                Text(
                    modifier = Modifier.padding(end = SpaceSize.large),
                    text = guide.name,
                    maxLines = 2,
                    style = AppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground,
                )

                GuideAndAuthorInfo(
                    guide = guide,
                    fontColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        VisibilityBadge(
            visibility = guide.visibility,
            modifier = Modifier.align(Alignment.TopEnd),
        )
    }
}

@Composable
private fun VisibilityBadge(visibility: GuideVisibility, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(visibility.badgeBackgroundColor()),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            painter = visibility.badgeIcon(),
            contentDescription = stringResource(visibility.badgeContentDescription()),
            tint = Color.White,
        )
    }
}

@Composable
@Preview
fun FavoriteGuideCardPreview() {
    AppTheme {
        FavoriteGuideCard(
            guide = Guide(
                id = "g1",
                name = "Michelin Star Favorites",
                description = "A curated collection of the finest dining experiences",
                visibility = GuideVisibility.PUBLIC,
                author = Author(id = "a1", name = "Sarah Mitchell", username = "sarahmitchell"),
                numberOfRestaurant = 8,
            ),
        )
    }
}
