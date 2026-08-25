package pt.socialfood.presentation.map

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UILabel

private const val PIN_HORIZONTAL_PADDING = 12.0
private const val PIN_VERTICAL_PADDING = 6.0
private const val PIN_FONT_SIZE = 12.0
private const val PIN_BORDER_WIDTH = 1.0
private const val PIN_MAX_FIT_SIZE = 1000.0
private val PinTintColor = UIColor(red = 0xE8 / 255.0, green = 0x45 / 255.0, blue = 0x0A / 255.0, alpha = 1.0)

@OptIn(ExperimentalForeignApi::class)
internal class RestaurantMarkerAnnotationView(annotation: MKAnnotationProtocol?, reuseIdentifier: String?) :
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
