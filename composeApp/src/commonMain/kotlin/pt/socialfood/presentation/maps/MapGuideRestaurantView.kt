package pt.socialfood.presentation.maps

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pt.socialfood.domain.model.Restaurant

@Composable
expect fun MapGuideRestaurantView(
    restaurants: List<Restaurant>,
    selectedRestaurantId: String?,
    onRestaurantSelected: (String) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier,
    showMarkerLabel: Boolean = true,
    dragGesturesEnabled: Boolean = true,
    zoomGesturesEnabled: Boolean = true,
)
