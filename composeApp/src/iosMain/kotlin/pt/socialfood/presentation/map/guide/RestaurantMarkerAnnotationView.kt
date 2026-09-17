package pt.socialfood.presentation.map.guide

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGMutablePathRef
import platform.CoreGraphics.CGPathAddCurveToPoint
import platform.CoreGraphics.CGPathAddLineToPoint
import platform.CoreGraphics.CGPathCreateMutable
import platform.CoreGraphics.CGPathMoveToPoint
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.MapKit.MKAnnotationProtocol
import platform.MapKit.MKAnnotationView
import platform.QuartzCore.CAShapeLayer
import platform.QuartzCore.kCALineCapRound
import platform.QuartzCore.kCALineJoinRound
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UILabel

private const val PIN_HORIZONTAL_PADDING = 12.0
private const val PIN_VERTICAL_PADDING = 6.0
private const val PIN_FONT_SIZE = 12.0
private const val PIN_BORDER_WIDTH = 1.0
private const val PIN_MAX_FIT_SIZE = 1000.0
private const val ICON_PIN_DIAMETER = 36.0
private const val ICON_INSET = 8.0
private const val ICON_VIEWPORT = 16.0
private const val ICON_STROKE_WIDTH = 1.333
private val PinTintColor = UIColor(red = 0xE8 / 255.0, green = 0x45 / 255.0, blue = 0x0A / 255.0, alpha = 1.0)

/** Mirrors the path data of drawable/restaurants_icon.xml (16x16 viewport) so both platforms render the same glyph. */
@Suppress("MagicNumber")
@OptIn(ExperimentalForeignApi::class)
private fun buildRestaurantIconPath(size: Double): CGMutablePathRef? {
    val scale = size / ICON_VIEWPORT
    val path = CGPathCreateMutable()

    fun moveTo(x: Double, y: Double) = CGPathMoveToPoint(path, null, x * scale, y * scale)
    fun lineTo(x: Double, y: Double) = CGPathAddLineToPoint(path, null, x * scale, y * scale)
    fun curveTo(x1: Double, y1: Double, x2: Double, y2: Double, x: Double, y: Double) =
        CGPathAddCurveToPoint(path, null, x1 * scale, y1 * scale, x2 * scale, y2 * scale, x * scale, y * scale)

    moveTo(10.664, 1.333)
    lineTo(9.131, 2.866)
    curveTo(8.765, 3.24, 8.56, 3.742, 8.56, 4.266)
    curveTo(8.56, 4.789, 8.765, 5.291, 9.131, 5.665)
    lineTo(10.331, 6.865)
    curveTo(10.705, 7.231, 11.207, 7.436, 11.731, 7.436)
    curveTo(12.254, 7.436, 12.756, 7.231, 13.13, 6.865)
    lineTo(14.663, 5.332)

    moveTo(9.998, 9.997)
    lineTo(2.2, 2.199)
    curveTo(1.934, 2.46, 1.722, 2.771, 1.578, 3.115)
    curveTo(1.434, 3.458, 1.359, 3.827, 1.359, 4.199)
    curveTo(1.359, 4.571, 1.434, 4.94, 1.578, 5.283)
    curveTo(1.722, 5.627, 1.934, 5.938, 2.2, 6.198)
    lineTo(7.065, 11.064)
    curveTo(7.532, 11.53, 8.398, 11.53, 8.931, 11.064)
    lineTo(9.998, 9.997)
    moveTo(9.998, 9.997)
    lineTo(14.663, 14.663)

    moveTo(1.399, 14.53)
    lineTo(5.665, 10.331)

    moveTo(12.663, 3.332)
    lineTo(7.998, 7.998)

    return path
}

@OptIn(ExperimentalForeignApi::class)
internal class RestaurantMarkerAnnotationView(annotation: MKAnnotationProtocol?, reuseIdentifier: String?) :
    MKAnnotationView(annotation, reuseIdentifier) {

    private val label = UILabel().apply {
        font = UIFont.boldSystemFontOfSize(PIN_FONT_SIZE)
        textAlignment = NSTextAlignmentCenter
        numberOfLines = 1
    }

    private val iconLayer = CAShapeLayer().apply {
        fillColor = null
        lineWidth = ICON_STROKE_WIDTH * (ICON_PIN_DIAMETER - ICON_INSET * 2) / ICON_VIEWPORT
        lineCap = kCALineCapRound
        lineJoin = kCALineJoinRound
        hidden = true
    }

    private var isSelectedNatively = false

    /** The location preview shows a single, always-prominent pin, so it always uses the "selected" colors. */
    private var forceSelectedAppearance = false

    init {
        canShowCallout = false
        clipsToBounds = true
        layer.borderWidth = PIN_BORDER_WIDTH
        layer.borderColor = PinTintColor.CGColor
        addSubview(label)
        layer.addSublayer(iconLayer)
        applyColors()
    }

    fun configure(name: String, showLabel: Boolean) {
        label.hidden = !showLabel
        iconLayer.hidden = showLabel
        forceSelectedAppearance = !showLabel

        if (showLabel) {
            label.text = name

            val (textWidth, textHeight) = label
                .sizeThatFits(CGSizeMake(PIN_MAX_FIT_SIZE, PIN_MAX_FIT_SIZE))
                .useContents { width to height }

            val width = textWidth + PIN_HORIZONTAL_PADDING * 2
            val height = textHeight + PIN_VERTICAL_PADDING * 2

            setFrame(CGRectMake(0.0, 0.0, width, height))
            label.setFrame(CGRectMake(PIN_HORIZONTAL_PADDING, PIN_VERTICAL_PADDING, textWidth, textHeight))
            layer.cornerRadius = height / 2.0
        } else {
            val iconSize = ICON_PIN_DIAMETER - ICON_INSET * 2

            setFrame(CGRectMake(0.0, 0.0, ICON_PIN_DIAMETER, ICON_PIN_DIAMETER))
            iconLayer.frame = CGRectMake(ICON_INSET, ICON_INSET, iconSize, iconSize)
            iconLayer.path = buildRestaurantIconPath(iconSize)
            layer.cornerRadius = ICON_PIN_DIAMETER / 2.0
        }

        applyColors()
    }

    override fun setSelected(selected: Boolean, animated: Boolean) {
        super.setSelected(selected, animated)
        isSelectedNatively = selected
        applyColors()
    }

    private fun applyColors() {
        if (isSelectedNatively || forceSelectedAppearance) {
            backgroundColor = PinTintColor
            label.textColor = UIColor.whiteColor
            iconLayer.strokeColor = UIColor.whiteColor.CGColor
        } else {
            backgroundColor = UIColor.whiteColor
            label.textColor = PinTintColor
            iconLayer.strokeColor = PinTintColor.CGColor
        }
    }
}
