package pt.socialfood.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.cValue
import platform.CoreLocation.CLLocationCoordinate2D
import platform.Foundation.NSSelectorFromString
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKPointAnnotation
import platform.UIKit.UITapGestureRecognizer
import platform.UIKit.UIView
import platform.darwin.NSObject
import pt.socialfood.domain.model.Restaurant

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class MapTapHandler : NSObject() {
    var onMapTapped: () -> Unit = {}

    @ObjCAction
    fun handleTap(recognizer: UITapGestureRecognizer) {
        val mapView = recognizer.view as? MKMapView ?: return
        val hitView = mapView.hitTest(recognizer.locationInView(mapView), withEvent = null)
        if (isAnnotationView(hitView)) return
        onMapTapped()
    }

    private fun isAnnotationView(view: UIView?): Boolean {
        var current = view
        while (current != null) {
            if (current is MKAnnotationView) return true
            current = current.superview
        }
        return false
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapRestaurantView(
    restaurants: List<Restaurant>,
    selectedRestaurantId: String?,
    onRestaurantSelected: (String) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier,
    showMarkerLabel: Boolean,
    dragGesturesEnabled: Boolean,
) {
    val mapDelegate = remember { RestaurantAnnotationDelegate() }
    val tapHandler = remember { MapTapHandler() }
    mapDelegate.onRestaurantSelected = onRestaurantSelected
    mapDelegate.showMarkerLabel = showMarkerLabel
    tapHandler.onMapTapped = onMapClick

    UIKitView(
        factory = {
            MKMapView().apply {
                delegate = mapDelegate
                addGestureRecognizer(
                    UITapGestureRecognizer(target = tapHandler, action = NSSelectorFromString("handleTap:")),
                )
            }
        },
        modifier = modifier,
        update = { mapView ->
            mapView.scrollEnabled = dragGesturesEnabled
            mapView.removeAnnotations(mapView.annotations)

            val annotations = restaurants.map { restaurant ->
                MKPointAnnotation(
                    coordinate = cValue<CLLocationCoordinate2D> {
                        latitude = restaurant.location.latitude
                        longitude = restaurant.location.longitude
                    },
                    title = restaurant.name,
                    subtitle = restaurant.id,
                )
            }

            if (annotations.isNotEmpty()) {
                mapView.addAnnotations(annotations)
                mapView.showAnnotations(annotations, animated = true)
            }
        },
    )
}
