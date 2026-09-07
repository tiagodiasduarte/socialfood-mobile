package pt.socialfood.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.Author
import pt.socialfood.presentation.components.UserImage
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.authors_separator
import socialfood.composeapp.generated.resources.authors_stat_followers_label
import socialfood.composeapp.generated.resources.authors_stat_following_label
import socialfood.composeapp.generated.resources.authors_stat_guides_label

private val AvatarSize = 48.dp
private const val UNAVAILABLE_STAT = "-"

@Composable
fun SearchAuthorItem(author: Author, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SpaceSize.medium, vertical = SpaceSize.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
        ) {
            UserImage(imageUrl = author.imageUrl, imageSize = AvatarSize)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = author.name,
                    style = AppTypography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = "@${author.username}",
                    style = AppTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(SpaceSize.medium))

                AuthorStats()
            }
        }
    }
}

@Composable
private fun AuthorStats() {
    val separator = stringResource(Res.string.authors_separator)
    val guidesLabel = stringResource(Res.string.authors_stat_guides_label)
    val followersLabel = stringResource(Res.string.authors_stat_followers_label)
    val followingLabel = stringResource(Res.string.authors_stat_following_label)
    val stats = listOf(guidesLabel, followersLabel, followingLabel)
        .joinToString(" $separator ") { "$UNAVAILABLE_STAT $it" }

    Text(
        text = stats,
        style = AppTypography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
@Preview
private fun SearchAuthorItemPreview() {
    AppTheme {
        SearchAuthorItem(author = Author(id = "1", name = "Michael Rodriguez", username = "mrodriguez"))
    }
}
