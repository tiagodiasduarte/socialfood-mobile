package pt.socialfood.presentation.guides

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.User
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.FavouriteRed
import pt.socialfood.ui.theme.SpaceSize

internal val CardHeight = 180.dp

@Composable
fun GuideCard(
    guide: Guide,
    width: Dp? = null,
    isFavourite: Boolean = false,
    onClick: () -> Unit = {},
    onFavouriteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = if (width != null) {
            modifier.width(width)
                .height(CardHeight)
                .clip(RoundedCornerShape(SpaceSize.large))
                .clickable(onClick = onClick)
        } else {
            modifier.fillMaxWidth()
                .height(CardHeight)
                .clip(RoundedCornerShape(SpaceSize.large))
                .clickable(onClick = onClick)
        }
    ) {
        // Background image or fallback color
        if (guide.imageUrl != null) {
            SubcomposeAsyncImage(
                model = guide.imageUrl,
                contentDescription = guide.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF2A2A2A)),
                    )
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF2A2A2A)),
                    )
                },
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2A2A2A)),
            )
        }

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.75f),
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY,
                    ),
                ),
        )

        if (onFavouriteClick != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
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

        // Text content
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

            if (guide.description.isNotBlank()) {
                Text(
                    text = guide.description,
                    style = AppTypography.headlineSmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

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
                    AuthorChip(author = it)
                }
            }
        }
    }
}

@Composable
private fun AuthorChip(author: Author) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val initials = author.name
            .trim()
            .split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifEmpty { "?" }

        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            if (author.imageUrl != null) {
                SubcomposeAsyncImage(
                    model = author.imageUrl,
                    contentDescription = author.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Text(
                            initials,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                        )
                    },
                    error = {
                        Text(
                            initials,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                        )
                    },
                )
            } else {
                Text(
                    initials,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
            }
        }

        Text(
            text = author.name,
            style = AppTypography.labelMedium,
            color = Color.White.copy(alpha = 0.9f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}