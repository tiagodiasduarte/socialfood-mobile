package pt.socialfood.presentation.map

import kotlinx.cinterop.ExperimentalForeignApi
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKPointAnnotation
import platform.darwin.NSObject

private const val ANNOTATION_REUSE_ID = "RestaurantAnnotation"

@OptIn(ExperimentalForeignApi::class)
internal class RestaurantAnnotationDelegate :
    NSObject(),
    MKMapViewDelegateProtocol {

    var onRestaurantSelected: (String) -> Unit = {}
    var showMarkerLabel: Boolean = true

    override fun mapView(mapView: MKMapView, viewForAnnotation: MKAnnotationProtocol): MKAnnotationView {
        val reusedView = mapView
            .dequeueReusableAnnotationViewWithIdentifier(ANNOTATION_REUSE_ID) as? RestaurantMarkerAnnotationView
        val annotationView = reusedView
            ?: RestaurantMarkerAnnotationView(annotation = viewForAnnotation, reuseIdentifier = ANNOTATION_REUSE_ID)

        annotationView.annotation = viewForAnnotation
        annotationView.configure(viewForAnnotation.title.orEmpty(), showMarkerLabel)

        return annotationView
    }

    override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
        val annotation = didSelectAnnotationView.annotation as? MKPointAnnotation ?: return
        val restaurantId = annotation.subtitle ?: return
        onRestaurantSelected(restaurantId)
    }
}
