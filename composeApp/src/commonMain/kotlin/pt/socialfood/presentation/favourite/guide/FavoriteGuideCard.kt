package pt.socialfood.presentation.favourite.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.favorite_card_remove_button_description
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.FavouriteRed
import pt.socialfood.ui.theme.SpaceSize

internal val CardHeight = 125.dp
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSize.large, vertical = SpaceSize.large),
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
                    text = guide.name,
                    style = AppTypography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "${guide.numberOfRestaurant} restaurants",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = AppTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(onClick = onRemoveClick) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(Res.string.favorite_card_remove_button_description),
                    tint = FavouriteRed,
                )
            }
        }
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
                author = Author(id = "a1", name = "Sarah Mitchell"),
                numberOfRestaurant = 8,
            ),
        )
    }
}
