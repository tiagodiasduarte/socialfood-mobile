package pt.socialfood.presentation.map.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.guide_map_close_button_description
import socialfood.composeapp.generated.resources.guide_map_restaurants_count_label
import socialfood.composeapp.generated.resources.restaurants_map_empty_message

@Composable
fun MapVisitRestaurantScreen(
    status: VisitStatus,
    title: String,
    onBackClick: () -> Unit,
    onRestaurantClick: (restaurantId: String) -> Unit = {},
    viewModel: MapVisitRestaurantViewModel = koinViewModel { parametersOf(status) },
) {
    val restaurants by viewModel.restaurants.collectAsStateWithLifecycle()

    MapVisitRestaurantContent(
        title = title,
        restaurants = restaurants,
        onBackClick = onBackClick,
        onRestaurantClick = onRestaurantClick,
    )
}

@Composable
private fun MapVisitRestaurantContent(
    title: String,
    restaurants: List<Restaurant>,
    onBackClick: () -> Unit,
    onRestaurantClick: (restaurantId: String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        MapVisitRestaurantHeader(
            title = title,
            restaurantsCount = restaurants.size,
            onCloseClick = onBackClick,
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (restaurants.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(SpaceSize.xlarge),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.restaurants_map_empty_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                MapRestaurantList(
                    restaurants = restaurants,
                    modifier = Modifier.fillMaxSize(),
                    onRestaurantClick = onRestaurantClick,
                )
            }
        }
    }
}

@Composable
private fun MapVisitRestaurantHeader(title: String, restaurantsCount: Int, onCloseClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = SpaceSize.large, vertical = SpaceSize.large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(SpaceSize.small)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(Res.string.guide_map_restaurants_count_label, restaurantsCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        IconButton(onClick = onCloseClick) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(Res.string.guide_map_close_button_description),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
@Preview
private fun MapVisitRestaurantScreenPreview() {
    val restaurants = listOf(
        "Le Jardin" to Location(latitude = 48.8566, longitude = 2.3522),
        "Terra" to Location(latitude = 48.8606, longitude = 2.3376),
    ).mapIndexed { index, (name, location) ->
        Restaurant(
            id = "r$index",
            name = name,
            description = "",
            city = "Paris",
            country = "France",
            countryCode = "FR",
            postalCode = "",
            imagesUrl = emptyList(),
            address = "",
            rating = 4.8,
            userRatingCount = 320,
            websiteUrl = "",
            phoneNumber = "",
            location = location,
        )
    }

    AppTheme {
        MapVisitRestaurantContent(
            title = "Visited Restaurants",
            restaurants = restaurants,
            onBackClick = {},
        )
    }
}

@Composable
@Preview
private fun MapVisitRestaurantScreenEmptyPreview() {
    AppTheme {
        MapVisitRestaurantContent(
            title = "Visited Restaurants",
            restaurants = emptyList(),
            onBackClick = {},
        )
    }
}
