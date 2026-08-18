package pt.socialfood.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.components.UserImage
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.BorderGrey
import pt.socialfood.ui.theme.GreyBackground
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.app_name
import socialfood.composeapp.generated.resources.home_subtitle_label
import socialfood.composeapp.generated.resources.home_title_label

@Composable
fun HomeHeader(
    userName: String = "",
    userImageUrl: String? = null,
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(GreyBackground),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .background(Color.White),
        ) {
            UserImage(
                name = userName,
                imageUrl = userImageUrl,
                imageSize = 36.dp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterStart)
                    .padding(horizontal = SpaceSize.large)
                    .clickable(onClick = onProfileClick),
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpaceSize.large)
                    .align(alignment = Alignment.Center),
                text = stringResource(Res.string.app_name),
                style = AppTypography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )

            HorizontalDivider(
                Modifier.height(1.dp)
                    .align(alignment = Alignment.BottomStart)
                    .background(BorderGrey),
            )
        }

        Spacer(Modifier.height(SpaceSize.xlarge))

        Text(
            modifier = Modifier.padding(horizontal = SpaceSize.large),
            text = stringResource(Res.string.home_title_label),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(SpaceSize.medium))

        Text(
            modifier = Modifier.padding(horizontal = SpaceSize.large),
            text = stringResource(Res.string.home_subtitle_label),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(Modifier.height(SpaceSize.xlarge))

        HomeSearchBar(
            searchQuery = "",
            onQueryChange = {},
            onClick = onSearchClick,
        )

        Spacer(Modifier.height(SpaceSize.large))
    }
}

@Composable
@Preview
fun HomeHeaderPreview() {
    AppTheme {
        HomeHeader()
    }
}
