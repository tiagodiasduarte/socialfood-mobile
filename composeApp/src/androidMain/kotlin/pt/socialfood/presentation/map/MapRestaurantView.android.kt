package pt.socialfood.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import pt.socialfood.domain.model.Restaurant

private const val SINGLE_PIN_ZOOM = 15f
private const val BOUNDS_PADDING_PX = 100

@Composable
actual fun MapRestaurantView(restaurants: List<Restaurant>, modifier: Modifier) {
    val cameraPositionState = rememberCameraPositionState()
    var selectedRestaurantId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(restaurants) {
        val points = restaurants.map { LatLng(it.location.latitude, it.location.longitude) }
        when {
            points.size == 1 -> cameraPositionState.move(
                CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(points.first(), SINGLE_PIN_ZOOM)),
            )

            points.isNotEmpty() -> {
                val bounds = LatLngBounds.Builder().apply { points.forEach { include(it) } }.build()
                cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, BOUNDS_PADDING_PX))
            }
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapClick = { selectedRestaurantId = null },
    ) {
        restaurants.forEach { restaurant ->
            val isSelected = restaurant.id == selectedRestaurantId
            MarkerComposable(
                keys = arrayOf<Any>(restaurant.id, isSelected),
                state = MarkerState(position = LatLng(restaurant.location.latitude, restaurant.location.longitude)),
                onClick = {
                    selectedRestaurantId = if (isSelected) null else restaurant.id
                    true
                },
            ) {
                MapRestaurantMarker(name = restaurant.name, selected = isSelected)
            }
        }
    }
}
