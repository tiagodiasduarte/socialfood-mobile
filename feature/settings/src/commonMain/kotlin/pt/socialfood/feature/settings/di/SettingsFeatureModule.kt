package pt.socialfood.feature.settings.di

import org.koin.dsl.module
import pt.socialfood.feature.settings.drawer.DrawerViewModel
import pt.socialfood.feature.settings.theme.ThemeViewModel

val settingsFeatureModule =
    module {
        factory { DrawerViewModel(get(), get(), get()) }
        factory { ThemeViewModel(get(), get()) }
    }
