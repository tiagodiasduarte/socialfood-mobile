package pt.socialfood.di

import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSBundle
import pt.socialfood.core.AppConfig

actual val platformModule: Module = module {
    single {
        val info = NSBundle.mainBundle.infoDictionary
        AppConfig(
            versionName = info?.get("CFBundleShortVersionString") as? String ?: "",
            buildDate = info?.get("BuildDate") as? String ?: "",
        )
    }
}
