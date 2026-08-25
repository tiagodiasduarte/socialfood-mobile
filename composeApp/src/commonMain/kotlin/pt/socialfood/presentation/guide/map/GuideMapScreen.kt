package pt.socialfood.presentation.guide.map

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
import androidx.compose.material3.CircularProgressIndicator
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
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.map.MapRestaurantView
import pt.socialfood.ui.theme.AppTheme
import pt.socialfood.ui.theme.SpaceSize
import socialfood.composeapp.generated.resources.Res
import socialfood.composeapp.generated.resources.guide_map_close_button_description
import socialfood.composeapp.generated.resources.guide_map_empty_message
import socialfood.composeapp.generated.resources.guide_map_locations_count_label

@Composable
fun GuideMapScreen(
    guideId: String,
    guideName: String,
    restaurantsCount: Int,
    onBackClick: () -> Unit,
    viewModel: GuideMapViewModel = koinViewModel { parametersOf(guideId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    GuideMapContent(
        state = state,
        guideName = guideName,
        restaurantsCount = restaurantsCount,
        onBackClick = onBackClick,
        onRetry = viewModel::load,
    )
}

@Composable
private fun GuideMapContent(
    state: GuideMapUiState,
    guideName: String,
    restaurantsCount: Int,
    onBackClick: () -> Unit,
    onRetry: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        GuideMapHeader(
            guideName = guideName,
            restaurantsCount = restaurantsCount,
            onCloseClick = onBackClick,
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (state) {
                GuideMapUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is GuideMapUiState.Loaded -> if (state.guide.restaurants.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(SpaceSize.xlarge),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.guide_map_empty_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    MapRestaurantView(restaurants = state.guide.restaurants, modifier = Modifier.fillMaxSize())
                }

                is GuideMapUiState.Error -> ErrorContent(
                    modifier = Modifier.fillMaxSize(),
                    onRetryClick = onRetry,
                )
            }
        }
    }
}

@Composable
private fun GuideMapHeader(guideName: String, restaurantsCount: Int, onCloseClick: () -> Unit) {
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
                text = guideName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(Res.string.guide_map_locations_count_label, restaurantsCount),
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
fun GuideMapScreenLoadingPreview() {
    AppTheme {
        GuideMapContent(
            state = GuideMapUiState.Loading,
            guideName = "Michelin Star Favorites",
            restaurantsCount = 4,
            onBackClick = {},
        )
    }
}

@Composable
@Preview
fun GuideMapScreenPreview() {
    val author = Author(id = "u1", name = "Sarah Mitchell", username = "sarahmitchell")
    val restaurants = listOf(
        "Le Jardin" to Location(latitude = 48.8566, longitude = 2.3522),
        "Terra" to Location(latitude = 48.8606, longitude = 2.3376),
        "Sakura" to Location(latitude = 48.8529, longitude = 2.3499),
        "Amber" to Location(latitude = 48.8496, longitude = 2.3441),
    ).mapIndexed { index, (name, location) ->
        Restaurant(
            id = "r$index",
            name = name,
            description = "",
            city = "Paris",
            country = "France",
            countryCode = "FR",
            postalCode = "",
            photoNames = emptyList(),
            address = "",
            rating = 4.8,
            userRatingCount = 320,
            websiteUrl = "",
            phoneNumber = "",
            location = location,
        )
    }

    val guide = Guide(
        id = "g1",
        name = "Michelin Star Favorites",
        description = "",
        numberOfRestaurant = restaurants.size,
        visibility = GuideVisibility.PUBLIC,
        author = author,
        restaurants = restaurants,
    )

    AppTheme {
        GuideMapContent(
            state = GuideMapUiState.Loaded(guide),
            guideName = guide.name,
            restaurantsCount = restaurants.size,
            onBackClick = {},
        )
    }
}

@Composable
@Preview
fun GuideMapScreenErrorPreview() {
    AppTheme {
        GuideMapContent(
            state = GuideMapUiState.Error(ErrorCode.GUIDE_NOT_FOUND),
            guideName = "Michelin Star Favorites",
            restaurantsCount = 4,
            onBackClick = {},
            onRetry = {},
        )
    }
}
