package pt.socialfood.presentation.restaurant.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import pt.socialfood.domain.model.Place
import pt.socialfood.presentation.search.SearchSectionHeader
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.search_restaurants_recent_searches_title

@Composable
fun RecentSearchesContent(places: List<Place>, onRestaurantClicked: (Place) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = SpaceSize.large,
            vertical = SpaceSize.large,
        ),
        verticalArrangement = Arrangement.spacedBy(SpaceSize.medium),
    ) {
        item {
            SearchSectionHeader(
                modifier = Modifier.padding(vertical = SpaceSize.medium),
                icon = Icons.Outlined.History,
                title = stringResource(Res.string.search_restaurants_recent_searches_title),
            )
        }

        items(places, key = { it.id }) { place ->
            PlaceItem(
                place = place,
                onAddClicked = { onRestaurantClicked(place) },
            )
        }
    }
}
