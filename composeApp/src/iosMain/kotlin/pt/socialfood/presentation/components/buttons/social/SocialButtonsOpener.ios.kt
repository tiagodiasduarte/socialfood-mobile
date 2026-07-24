package pt.socialfood.presentation.components.buttons.social

import androidx.compose.runtime.Composable
import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.UIKit.UIApplication

private fun SocialPlatform.appUri(url: String): String? = when (this) {
    SocialPlatform.FACEBOOK -> {
        val encodedUrl = (url as NSString).stringByAddingPercentEncodingWithAllowedCharacters(
            NSCharacterSet.URLQueryAllowedCharacterSet,
        ) ?: url
        "fb://facewebmodal/f?href=$encodedUrl"
    }

    SocialPlatform.INSTAGRAM -> {
        val username = url.substringBefore('?').trimEnd('/').substringAfterLast('/')
        username.takeIf { it.isNotBlank() }?.let { "instagram://user?username=$it" }
    }

    SocialPlatform.YOUTUBE -> url.replaceFirst(Regex("^https?://(www\\.)?"), "youtube://")
}

@Composable
actual fun rememberSocialLinkOpener(): (socialPlatform: SocialPlatform, url: String) -> Unit {
    return { socialPlatform, url ->
        val application = UIApplication.sharedApplication
        val appUrl = socialPlatform.appUri(url)?.let { NSURL(string = it) }
        val webUrl = NSURL(string = url)

        if (appUrl != null && application.canOpenURL(appUrl)) {
            application.openURL(appUrl, options = emptyMap<Any?, Any?>(), completionHandler = null)
        } else {
            application.openURL(webUrl, options = emptyMap<Any?, Any?>(), completionHandler = null)
        }
    }
}