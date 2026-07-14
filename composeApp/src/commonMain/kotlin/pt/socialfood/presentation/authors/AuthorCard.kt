package pt.socialfood.presentation.authors

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
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
import pt.socialfood.presentation.authors.follow.FollowButton
import pt.socialfood.presentation.components.UserImage
import pt.socialfood.presentation.components.card.SectionCard
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun AuthorCard(
    author: Author,
    onFollowClick: (Author) -> Unit,
    onAuthorClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    SectionCard(modifier = modifier.wrapContentHeight()) {
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = author.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    Spacer(Modifier.width(SpaceSize.large))

                    FollowButton(
                        authorId = author.id,
                        isFollowing = author.isFollowing,
                        onFollowClick = { onFollowClick(author) }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "Lisbon",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

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
private fun AuthorCardFollowPreview() {
    AppTheme {
        AuthorCard(
            author = Author(
                id = "1",
                name = "Sarah Mitchell",
                isFollowing = false,
            ),
            onFollowClick = {},
        )
    }
}

@Preview
@Composable
private fun AuthorCardFollowingPreview() {
    AppTheme {
        AuthorCard(
            author = Author(
                id = "2",
                name = "Michael Rodriguez",
                isFollowing = true,
            ),
            onFollowClick = {},
        )
    }
}