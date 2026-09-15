package pt.socialfood.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pt.socialfood.domain.model.Restaurant

@Composable
expect fun MapRestaurantView(
    restaurants: List<Restaurant>,
    selectedRestaurantId: String?,
    onRestaurantSelected: (String) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier,
    showMarkerLabel: Boolean = true,
    dragGesturesEnabled: Boolean = true,
    zoomGesturesEnabled: Boolean = true,
)
