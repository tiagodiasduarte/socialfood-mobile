package pt.socialfood.presentation.guide.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.guides_add_button_description
import socialfood.composeapp.generated.resources.guides_search_placeholder
import socialfood.composeapp.generated.resources.guides_tab_all
import socialfood.composeapp.generated.resources.guides_tab_my
import socialfood.composeapp.generated.resources.guides_title
import pt.socialfood.presentation.components.SearchBar
import pt.socialfood.presentation.components.TopTabs
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize

@Composable
fun GuidesHeader(
    selectedTab: Int,
    searchQuery: String,
    onSelectedTab: (Int) -> Unit,
    onQueryChange: (String) -> Unit,
    onAddClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tabs = listOf(stringResource(Res.string.guides_tab_all), stringResource(Res.string.guides_tab_my))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.guides_title),
                style = AppTypography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(Res.string.guides_add_button_description),
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onAddClick() },
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(8.dp))

        SearchBar(
            searchQuery = searchQuery,
            onQueryChange = onQueryChange,
            placeholder = stringResource(Res.string.guides_search_placeholder)
        )

        Spacer(Modifier.height(SpaceSize.large))

        TopTabs(
            selectedTab = selectedTab,
            tabs = tabs,
            onTabSelected = { onSelectedTab(it) }
        )

        Spacer(Modifier.height(SpaceSize.large))
    }
}

@Composable
@Preview
fun GuidesHeaderPreview() {
    AppTheme {
        GuidesHeader(
            selectedTab = 0,
            searchQuery = "",
            onSelectedTab = { },
            onQueryChange = { }
        )
    }
}