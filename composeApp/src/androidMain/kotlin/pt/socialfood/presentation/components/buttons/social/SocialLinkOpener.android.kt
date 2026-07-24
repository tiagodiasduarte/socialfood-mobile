package pt.socialfood.presentation.components.buttons.social

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

private val SocialPlatform.packageName: String
    get() = when (this) {
        SocialPlatform.FACEBOOK -> "com.facebook.katana"
        SocialPlatform.INSTAGRAM -> "com.instagram.android"
        SocialPlatform.YOUTUBE -> "com.google.android.youtube"
    }

@Composable
actual fun rememberSocialLinkOpener(): (socialPlatform: SocialPlatform, url: String) -> Unit {
    val context = LocalContext.current
    return { socialPlatform, url ->
        val uri = url.toUri()
        val appIntent = Intent(Intent.ACTION_VIEW, uri).setPackage(socialPlatform.packageName)
        val openedApp = context.tryStartActivity(appIntent)
        if (!openedApp) {
            // No app installed for this platform (or the URL is invalid) — fall back to the browser.
            context.tryStartActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }
}

private fun Context.tryStartActivity(intent: Intent): Boolean = try {
    startActivity(intent)
    true
} catch (_: ActivityNotFoundException) {
    false
}