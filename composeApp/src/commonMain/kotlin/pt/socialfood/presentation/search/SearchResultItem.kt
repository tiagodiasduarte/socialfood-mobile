package pt.socialfood.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.socialfood.domain.model.Search
import pt.socialfood.domain.model.SearchResultType
import pt.socialfood.presentation.components.UserImage
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize

private val AvatarSize = 48.dp
private val BadgeSize = 18.dp

@Composable
fun SearchResultItem(result: Search, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = SpaceSize.large, vertical = SpaceSize.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        Box {
            UserImage(name = result.name, imageUrl = result.imageUrl, imageSize = AvatarSize)

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(BadgeSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = result.type.icon(),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp),
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.name,
                style = AppTypography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (result.description.isNotBlank()) {
                Text(
                    text = result.description,
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun SearchResultType.icon(): ImageVector = when (this) {
    SearchResultType.AUTHOR -> Icons.Outlined.Person
    SearchResultType.GUIDE -> Icons.AutoMirrored.Outlined.MenuBook
    SearchResultType.RESTAURANT -> Icons.Outlined.Restaurant
}

@Composable
@Preview
private fun SearchResultItemPreview() {
    AppTheme {
        SearchResultItem(
            result = Search(
                id = "1",
                name = "Belcanto",
                description = "Fine dining in Lisbon",
                type = SearchResultType.RESTAURANT,
            ),
        )
    }
}
