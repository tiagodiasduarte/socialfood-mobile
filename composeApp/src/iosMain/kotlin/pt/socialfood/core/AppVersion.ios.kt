package pt.socialfood.core

import platform.Foundation.NSBundle

actual val appVersion: String =
    NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: ""
