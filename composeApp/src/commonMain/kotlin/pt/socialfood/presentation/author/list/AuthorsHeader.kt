package pt.socialfood.presentation.author.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.components.UserImage
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.authors_title

@Composable
fun AuthorsHeader(userImageUrl: String? = null, onProfileClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSize.large),
        ) {
            UserImage(
                imageUrl = userImageUrl,
                imageSize = 32.dp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterStart)
                    .clickable(onClick = onProfileClick),
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.Center),
                text = stringResource(Res.string.authors_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(SpaceSize.medium))
    }
}

@Composable
@Preview
fun AuthorsHeaderPreview() {
    AppTheme {
        AuthorsHeader()
    }
}
