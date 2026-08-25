package pt.socialfood.presentation.map

import kotlinx.cinterop.ExperimentalForeignApi
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.darwin.NSObject

private const val ANNOTATION_REUSE_ID = "RestaurantAnnotation"

@OptIn(ExperimentalForeignApi::class)
internal class RestaurantAnnotationDelegate :
    NSObject(),
    MKMapViewDelegateProtocol {
    override fun mapView(mapView: MKMapView, viewForAnnotation: MKAnnotationProtocol): MKAnnotationView {
        val reusedView = mapView
            .dequeueReusableAnnotationViewWithIdentifier(ANNOTATION_REUSE_ID) as? RestaurantPinAnnotationView
        val annotationView = reusedView
            ?: RestaurantPinAnnotationView(annotation = viewForAnnotation, reuseIdentifier = ANNOTATION_REUSE_ID)

        annotationView.annotation = viewForAnnotation
        annotationView.configure(viewForAnnotation.title.orEmpty())

        return annotationView
    }
}
