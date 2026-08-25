package pt.socialfood.presentation.guide.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.ui.theme.SpaceSize

private const val SINGLE_PIN_ZOOM = 15f
private const val BOUNDS_PADDING_PX = 100
private val PinTailWidth = 12.dp
private val PinTailHeight = 6.dp
private val PinBorderWidth = 1.dp

private val PinTailShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width / 2f, size.height)
    close()
}

@Composable
actual fun RestaurantMapView(restaurants: List<Restaurant>, modifier: Modifier) {
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
                RestaurantMapPin(name = restaurant.name, selected = isSelected)
            }
        }
    }
}

@Composable
private fun RestaurantMapPin(name: String, selected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(SpaceSize.large))
                .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                .border(PinBorderWidth, MaterialTheme.colorScheme.primary, RoundedCornerShape(SpaceSize.large))
                .padding(horizontal = SpaceSize.medium, vertical = SpaceSize.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            )
        }
        Box(
            modifier = Modifier
                .size(width = PinTailWidth, height = PinTailHeight)
                .clip(PinTailShape)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}
