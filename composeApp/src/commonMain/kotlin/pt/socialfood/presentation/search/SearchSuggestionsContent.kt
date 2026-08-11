package pt.socialfood.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.AppTypography
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.author_icon
import socialfood.composeapp.generated.resources.guides_icon
import socialfood.composeapp.generated.resources.restaurants_icon
import socialfood.composeapp.generated.resources.search_suggestion_favorite_guides
import socialfood.composeapp.generated.resources.search_suggestion_favorite_restaurants
import socialfood.composeapp.generated.resources.search_suggestion_most_followed
import socialfood.composeapp.generated.resources.search_suggestion_trending_guides
import socialfood.composeapp.generated.resources.search_suggestions_title

@Composable
fun SearchSuggestionsContent(
    modifier: Modifier = Modifier,
    onFavoriteGuidesClick: () -> Unit = {},
    onFavoriteRestaurantsClick: () -> Unit = {},
    onTrendingGuidesClick: () -> Unit = {},
    onMostFollowedClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceSize.large, vertical = SpaceSize.large),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        Text(
            text = stringResource(Res.string.search_suggestions_title).uppercase(),
            style = AppTypography.labelLarge.copy(letterSpacing = 1.sp),
            color = MaterialTheme.colorScheme.onBackground,
        )
        SearchSuggestionItem(
            icon = painterResource(Res.drawable.guides_icon),
            label = stringResource(Res.string.search_suggestion_favorite_guides),
            onClick = onFavoriteGuidesClick,
        )
        Spacer(Modifier.height(SpaceSize.large))
        SearchSuggestionItem(
            icon = painterResource(Res.drawable.restaurants_icon),
            label = stringResource(Res.string.search_suggestion_favorite_restaurants),
            onClick = onFavoriteRestaurantsClick,
        )
        SearchSuggestionItem(
            icon = rememberVectorPainter(Icons.AutoMirrored.Filled.TrendingUp),
            label = stringResource(Res.string.search_suggestion_trending_guides),
            onClick = onTrendingGuidesClick,
        )
        SearchSuggestionItem(
            icon = painterResource(Res.drawable.author_icon),
            label = stringResource(Res.string.search_suggestion_most_followed),
            onClick = onMostFollowedClick,
        )
    }
}

@Composable
private fun SearchSuggestionItem(icon: Painter, label: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(SpaceSize.large),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SpaceSize.small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(SpaceSize.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceSize.large),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Text(
                text = label,
                style = AppTypography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
@Preview
private fun SearchSuggestionsContentPreview() {
    AppTheme {
        SearchSuggestionsContent()
    }
}
