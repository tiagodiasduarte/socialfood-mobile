package pt.socialfood.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.presentation.components.TopActionBar
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SearchBorder
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.app_name
import socialfood.composeapp.generated.resources.home_subtitle_label
import socialfood.composeapp.generated.resources.home_title_label

private val HeaderHeight = 65.dp

@Composable
fun HomeHeader(userImageUrl: String? = null, onProfileClick: () -> Unit = {}, onSearchClick: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background),
    ) {
        TopActionBar(
            title = stringResource(Res.string.app_name),
            userImageUrl = userImageUrl,
            onProfileClick = onProfileClick,
            height = HeaderHeight,
        )

        HorizontalDivider(
            modifier = Modifier
                .height(1.dp)
                .background(SearchBorder),
        )

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
