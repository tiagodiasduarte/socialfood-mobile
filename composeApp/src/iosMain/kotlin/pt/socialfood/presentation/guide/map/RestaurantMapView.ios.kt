package pt.socialfood.presentation.guide.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.CoreLocation.CLLocationCoordinate2D
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKPointAnnotation
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UILabel
import platform.darwin.NSObject
import pt.socialfood.domain.model.Restaurant

private const val ANNOTATION_REUSE_ID = "RestaurantAnnotation"
private const val PIN_HORIZONTAL_PADDING = 12.0
private const val PIN_VERTICAL_PADDING = 6.0
private const val PIN_FONT_SIZE = 12.0
private const val PIN_BORDER_WIDTH = 1.0
private const val PIN_MAX_FIT_SIZE = 1000.0
private val PinTintColor = UIColor(red = 0xE8 / 255.0, green = 0x45 / 255.0, blue = 0x0A / 255.0, alpha = 1.0)

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun RestaurantMapView(restaurants: List<Restaurant>, modifier: Modifier) {
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

@OptIn(ExperimentalForeignApi::class)
private class RestaurantAnnotationDelegate :
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

@OptIn(ExperimentalForeignApi::class)
private class RestaurantPinAnnotationView(annotation: MKAnnotationProtocol?, reuseIdentifier: String?) :
    MKAnnotationView(annotation, reuseIdentifier) {

    private val label = UILabel().apply {
        font = UIFont.boldSystemFontOfSize(PIN_FONT_SIZE)
        textAlignment = NSTextAlignmentCenter
        numberOfLines = 1
    }

    init {
        canShowCallout = false
        clipsToBounds = true
        layer.borderWidth = PIN_BORDER_WIDTH
        layer.borderColor = PinTintColor.CGColor
        addSubview(label)
        applyColors(selected = false)
    }

    fun configure(name: String) {
        label.text = name

        val (textWidth, textHeight) = label
            .sizeThatFits(CGSizeMake(PIN_MAX_FIT_SIZE, PIN_MAX_FIT_SIZE))
            .useContents { width to height }

        val width = textWidth + PIN_HORIZONTAL_PADDING * 2
        val height = textHeight + PIN_VERTICAL_PADDING * 2

        setFrame(CGRectMake(0.0, 0.0, width, height))
        label.setFrame(CGRectMake(PIN_HORIZONTAL_PADDING, PIN_VERTICAL_PADDING, textWidth, textHeight))
        layer.cornerRadius = height / 2.0
    }

    override fun setSelected(selected: Boolean, animated: Boolean) {
        super.setSelected(selected, animated)
        applyColors(selected)
    }

    private fun applyColors(selected: Boolean) {
        if (selected) {
            backgroundColor = PinTintColor
            label.textColor = UIColor.whiteColor
        } else {
            backgroundColor = UIColor.whiteColor
            label.textColor = PinTintColor
        }
    }
}
