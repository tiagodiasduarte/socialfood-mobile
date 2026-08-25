package pt.socialfood.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import pt.socialfood.domain.model.Restaurant

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapRestaurantView(restaurants: List<Restaurant>, modifier: Modifier) {
    val mapDelegate = remember { RestaurantAnnotationDelegate() }

    UIKitView(
        factory = {
            MKMapView().apply {
                delegate = mapDelegate
            }
        },
        modifier = modifier,
        update = { mapView ->
            mapView.removeAnnotations(mapView.annotations)

            val annotations = restaurants.map { restaurant ->
                MKPointAnnotation(
                    coordinate = cValue<CLLocationCoordinate2D> {
                        latitude = restaurant.location.latitude
                        longitude = restaurant.location.longitude
                    },
                    title = restaurant.name,
                    subtitle = null,
                )
            }

            if (annotations.isNotEmpty()) {
                mapView.addAnnotations(annotations)
                mapView.showAnnotations(annotations, animated = true)
            }
        },
    )
}
