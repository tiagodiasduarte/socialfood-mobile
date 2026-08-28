package pt.socialfood.presentation.search

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil3.compose.SubcomposeAsyncImage
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.presentation.components.UserImage
import pt.socialfood.presentation.components.cardImageScrim
import pt.socialfood.presentation.components.placeholder.GuideCardPlaceholder
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize

private val CardHeight = 150.dp

@Composable
fun SearchGuideItem(guide: Guide, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CardHeight)
            .clip(RoundedCornerShape(SpaceSize.large))
            .clickable(onClick = onClick),
    ) {
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

        Box(modifier = Modifier.fillMaxSize().cardImageScrim())

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = guide.name,
                style = AppTypography.headlineMedium,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "${guide.numberOfRestaurant} restaurants",
                    style = AppTypography.labelMedium,
                    color = Color.White.copy(alpha = 0.9f),
                )

                guide.author.let {
                    Text(
                        text = "•",
                        style = AppTypography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f),
                    )

                    UserImage(imageUrl = guide.author.imageUrl, imageSize = 20.dp)
                }
            }
        }
    }
}

@Composable
@Preview
private fun SearchGuideItemPreview() {
    AppTheme {
        SearchGuideItem(
            guide = Guide(
                id = "1",
                name = "Michelin Star Favorites",
                description = "The finest dining experiences",
                visibility = GuideVisibility.PUBLIC,
                author = Author(id = "a1", name = "Sarah M.", username = "sarahm"),
                numberOfRestaurant = 8,
            ),
        )
    }
}
