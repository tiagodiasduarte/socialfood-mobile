package pt.socialfood.presentation.author.follow

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.authors_follow_button
import socialfood.composeapp.generated.resources.authors_following_button
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun FollowButton(
    authorId: String,
    isFollowing: Boolean,
    onFollowClick: (String) -> Unit,
) {
    if (isFollowing) {
        OutlinedButton(
            onClick = { onFollowClick(authorId) },
            shape = RoundedCornerShape(SpaceSize.medium),
            contentPadding = PaddingValues(
                horizontal = SpaceSize.large,
                vertical = SpaceSize.small,
            ),
        ) {
            Text(
                text = stringResource(Res.string.authors_following_button),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    } else {
        Button(
            onClick = { onFollowClick(authorId) },
            shape = RoundedCornerShape(SpaceSize.medium),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            contentPadding = PaddingValues(
                horizontal = SpaceSize.large,
                vertical = SpaceSize.small,
            ),
        ) {
            Text(
                text = stringResource(Res.string.authors_follow_button),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
