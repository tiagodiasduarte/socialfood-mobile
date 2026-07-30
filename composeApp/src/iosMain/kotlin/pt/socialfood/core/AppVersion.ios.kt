package pt.socialfood.core

import platform.Foundation.NSBundle

actual val appVersion: String =
    NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: ""

actual val appBuildDate: String =
    NSBundle.mainBundle.infoDictionary?.get("BuildDate") as? String ?: ""
