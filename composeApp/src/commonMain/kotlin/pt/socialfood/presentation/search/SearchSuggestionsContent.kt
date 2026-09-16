package pt.socialfood.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.RecentSearch
import pt.socialfood.domain.model.RecentSearchType
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.author_icon
import socialfood.composeapp.generated.resources.guide_icon
import socialfood.composeapp.generated.resources.restaurant_icon
import socialfood.composeapp.generated.resources.search_recent_searches_title
import socialfood.composeapp.generated.resources.search_suggestion_favorite_guides
import socialfood.composeapp.generated.resources.search_suggestion_favorite_restaurants
import socialfood.composeapp.generated.resources.search_suggestion_most_followed
import socialfood.composeapp.generated.resources.search_suggestions_title

@Composable
fun SearchSuggestionsContent(
    modifier: Modifier = Modifier,
    recentSearches: List<RecentSearch> = emptyList(),
    onRecentSearchClick: (RecentSearch) -> Unit = {},
    onFavoriteGuidesClick: () -> Unit = {},
    onFavoriteRestaurantsClick: () -> Unit = {},
    onMostFollowedClick: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (recentSearches.isNotEmpty()) {
            Spacer(modifier = Modifier.height(SpaceSize.medium))
            SearchSectionHeader(
                modifier = Modifier.padding(horizontal = SpaceSize.large, vertical = SpaceSize.medium),
                icon = Icons.Outlined.History,
                title = stringResource(Res.string.search_recent_searches_title),
            )
            recentSearches.forEach { recentSearch ->
                SearchSuggestionItem(
                    icon = painterResource(recentSearch.type.iconRes()),
                    label = recentSearch.title,
                    onClick = { onRecentSearchClick(recentSearch) },
                )
                HorizontalDivider()
            }
        }

        Spacer(modifier = Modifier.height(SpaceSize.medium))
        SearchSectionHeader(
            modifier = Modifier.padding(horizontal = SpaceSize.large, vertical = SpaceSize.medium),
            icon = Icons.Outlined.Lightbulb,
            title = stringResource(Res.string.search_suggestions_title),
        )
        Spacer(modifier = Modifier.height(SpaceSize.medium))
        SearchSuggestionItem(
            icon = painterResource(Res.drawable.guide_icon),
            label = stringResource(Res.string.search_suggestion_favorite_guides),
            onClick = onFavoriteGuidesClick,
        )
        HorizontalDivider()
        SearchSuggestionItem(
            icon = painterResource(Res.drawable.restaurant_icon),
            label = stringResource(Res.string.search_suggestion_favorite_restaurants),
            onClick = onFavoriteRestaurantsClick,
        )
        HorizontalDivider()
        SearchSuggestionItem(
            icon = painterResource(Res.drawable.author_icon),
            label = stringResource(Res.string.search_suggestion_most_followed),
            onClick = onMostFollowedClick,
        )
        HorizontalDivider()
    }
}

private fun RecentSearchType.iconRes() = when (this) {
    RecentSearchType.RESTAURANT -> Res.drawable.restaurant_icon
    RecentSearchType.GUIDE -> Res.drawable.guide_icon
    RecentSearchType.AUTHOR -> Res.drawable.author_icon
}

@Composable
private fun SearchSuggestionItem(icon: Painter, label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(SpaceSize.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = label,
            style = AppTypography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
@Preview
private fun SearchSuggestionsContentPreview() {
    AppTheme {
        SearchSuggestionsContent()
    }
}
