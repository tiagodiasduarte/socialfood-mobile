package pt.socialfood.presentation.author.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.authors_separator
import socialfood.composeapp.generated.resources.authors_stat_followers_label
import socialfood.composeapp.generated.resources.authors_stat_following_label
import socialfood.composeapp.generated.resources.authors_stat_guides_label
import pt.socialfood.domain.model.Author
import pt.socialfood.presentation.components.UserImage
import pt.socialfood.presentation.components.card.SectionCard
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

val AuthorCardHeight = 112.dp

@Composable
fun AuthorCard(
    author: Author,
    onAuthorClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    SectionCard(modifier = modifier.height(AuthorCardHeight)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onAuthorClick),
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            UserImage(
                imageUrl = author.imageUrl,
                name = author.name,
                imageSize = 56.dp
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = author.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = "@${author.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(SpaceSize.large))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(SpaceSize.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatItem(
                        value = "-",
                        label = stringResource(Res.string.authors_stat_guides_label)
                    )
                    Text(
                        text = stringResource(Res.string.authors_separator),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    StatItem(
                        value = "-",
                        label = stringResource(Res.string.authors_stat_followers_label)
                    )

                    Text(
                        text = stringResource(Res.string.authors_separator),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    StatItem(
                        value = "-",
                        label = stringResource(Res.string.authors_stat_following_label)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun AuthorCardPreview() {
    AppTheme {
        AuthorCard(
            author = Author(
                id = "1",
                name = "Sarah Mitchell",
                username = "sarahmitchell",
            ),
        )
    }
}
