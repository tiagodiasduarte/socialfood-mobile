package pt.socialfood.presentation.map.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Location
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.presentation.components.ErrorContent
import pt.socialfood.presentation.components.TopActionBar
import pt.socialfood.presentation.map.guide.MapGuideRestaurantView
import pt.socialfood.ui.theme.AppTheme

@Composable
fun MapRestaurantScreen(
    restaurantId: String,
    onBackClick: () -> Unit,
    viewModel: MapRestaurantViewModel = koinViewModel { parametersOf(restaurantId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MapRestaurantContent(
        state = state,
        onBackClick = onBackClick,
        onRetry = viewModel::load,
    )
}

@Composable
private fun MapRestaurantContent(state: MapRestaurantUiState, onBackClick: () -> Unit, onRetry: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        TopActionBar(onBackClick = onBackClick)

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (state) {
                MapRestaurantUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

                is MapRestaurantUiState.Loaded -> MapGuideRestaurantView(
                    restaurants = listOf(state.restaurant),
                    selectedRestaurantId = state.restaurant.id,
                    onRestaurantSelected = {},
                    onMapClick = {},
                    showMarkerLabel = false,
                    modifier = Modifier.fillMaxSize(),
                )

                is MapRestaurantUiState.Error -> ErrorContent(
                    modifier = Modifier.fillMaxSize(),
                    onRetryClick = onRetry,
                )
            }
        }
    }
}

@Composable
@Preview
fun MapRestaurantScreenLoadingPreview() {
    AppTheme {
        MapRestaurantContent(state = MapRestaurantUiState.Loading, onBackClick = {})
    }
}

@Composable
@Preview
fun MapRestaurantScreenLoadedPreview() {
    val restaurant = Restaurant(
        id = "r1",
        name = "Le Jardin",
        description = "",
        city = "Lisbon",
        country = "Portugal",
        countryCode = "PT",
        postalCode = "1000-000",
        imagesUrl = emptyList(),
        address = "Rua Augusta 123, Lisbon",
        rating = 4.8,
        userRatingCount = 320,
        websiteUrl = "",
        phoneNumber = "+351 910 000 000",
        location = Location(latitude = 38.7223, longitude = -9.1393),
    )

    AppTheme {
        MapRestaurantContent(state = MapRestaurantUiState.Loaded(restaurant), onBackClick = {})
    }
}

@Composable
@Preview
fun MapRestaurantScreenErrorPreview() {
    AppTheme {
        MapRestaurantContent(
            state = MapRestaurantUiState.Error(ErrorCode.RESTAURANT_NOT_FOUND),
            onBackClick = {},
            onRetry = {},
        )
    }
}
