package pt.socialfood.presentation.imagepicker

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image as SkiaImage

actual fun ByteArray.toImageBitmap(): ImageBitmap = SkiaImage.makeFromEncoded(this).toComposeImageBitmap()
